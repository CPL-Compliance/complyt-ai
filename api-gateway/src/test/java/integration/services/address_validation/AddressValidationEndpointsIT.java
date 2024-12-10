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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        JSONObject address = TestUtilities.createAddressJsonExample(city, country, city, state, street, zip, false);

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL)
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
        JSONObject address = TestUtilities.createAddressJsonExample("Beverly Hills", "US", null, "CA", "1008 Elden Way", "90210", false);
        String expectedAddress = "{city=Beverly Hills, country=US, county=Los Angeles, state=CA, street=1008 Elden Way, zip=90210, isPartial=false}";

        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL)
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
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL)
                        .queryParam("state", "Alabama")
                        .queryParam("zip", "00000")
                        .queryParam("country", "Utopia")
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
    @Order(2)
    public void getAddress_MismatchesZip_Returns400() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.ADDRESS_VALIDATION_BASE_URL)
                        .queryParam("state", "CA")
                        .queryParam("zip", "12345")
                        .queryParam("city", "Beverly Hills")
                        .queryParam("street", "1008 Elden Way")
                        .queryParam("country", country)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(error -> {
                    assertEquals("ERR-ADDR-002: The ZIP code you provided (12345) does not match the address entered. Did you mean ZIP code 90210 for the address 'Address[city=Beverly Hills, country=US, county=null, state=CA, street=1008 Elden Way, zip=12345, isPartial=false]'?", error.get("message"));
                });
    }
}
