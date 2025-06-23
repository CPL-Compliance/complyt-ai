package integration.services.address_validation;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddressValidationEndpointsIT extends TestContainersInitializerIT implements AddressValidationEndpointsITTemplate {
    String state = "New York";
    String country = "US";
    String street = "160 Broadway";
    String zip = "10013";

    @Test
    @Override
    public void getAddress_ValidAndInCache_Returns200() {
        String city = state;
        JSONObject address = TestUtilities.createMatchedAddressJsonExample(city, country, city, state, street, zip);

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL + "/resolve")
                        .queryParam("state", state)
                        .queryParam("zip", zip)
                        .queryParam("city", city)
                        .queryParam("street", street)
                        .queryParam("country", country)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkedHashMap.class)
                .value(addressResult -> {
                    assertEquals(address, addressResult);
                });
    }

    @Test
    @Override
    public void getAddress_ValidButNotCached_Returns200() {
        // Here Stub Address
        String expectedAddress = "{address={city=Beverly Hills, country=US, county=Los Angeles, state=CA, street=1008 Elden Way, zip=90210, isPartial=true}, " +
                "scoring={matchLevel=EXCELLENT, score=0.9, fieldScore={countryMatch=EXACT, stateMatch=EXACT, cityMatch=EXACT, streetMatch=EXACT, zipMatch=EXACT}}}";

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL + "/resolve")
                        .queryParam("state", "CA")
                        .queryParam("zip", "90210")
                        .queryParam("city", "Beverly Hills")
                        .queryParam("street", "1008 Elden Way")
                        .queryParam("country", country)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkedHashMap.class)
                .value(addressResult -> {
                    assertEquals(addressResult.toString(), expectedAddress);
                });
    }

    @Test
    @Order(0)
    @Override
    public void getAddress_NotValidAddress_Returns400() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL + "/resolve")
                        .queryParam("state", "Alabama")
                        .queryParam("zip", "00000")
                        .queryParam("country", "US")
                        .queryParam("city", "Faketown")
                        .queryParam("isPartial", true)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(error -> {
                    assertEquals("ERR-ADDR-001: The address could not be validated. Please check that the street, city, state, and ZIP are correct and properly formatted.", error.get("message"));
                });
    }


    @Test
    @Order(0)
    public void validate_ValidAndInCache_Returns200() {
        String city = state;

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL + "/validate")
                        .queryParam("state", state)
                        .queryParam("zip", zip)
                        .queryParam("city", city)
                        .queryParam("street", street)
                        .queryParam("country", country)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkedHashMap.class)
                .value(addressResult -> {
                    System.out.println(addressResult);
                    LinkedHashMap<String, Object> addressDto = (LinkedHashMap<String, Object>) ((List<?>) addressResult.get("matchedAddresses")).get(0);
                    assertNotNull(addressDto);
                });
    }
}
