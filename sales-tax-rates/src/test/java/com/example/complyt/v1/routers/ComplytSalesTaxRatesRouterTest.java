package com.example.complyt.v1.routers;

import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.RatesMetaData;
import com.complyt.facade.ComplytSalesTaxRatesFacade;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handler.ComplytSalesTaxRatesHandler;
import com.complyt.v1.mappers.AddressMapper;
import com.complyt.v1.mappers.ComplytSalesTaxRatesMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ComplytSalesTaxRatesDto;
import com.complyt.v1.router.ComplytSalesTaxRatesRouter;
import com.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import com.example.complyt.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ComplytSalesTaxRatesRouter.class, ComplytSalesTaxRatesHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalExceptionHandler.class,
        GlobalErrorAttributes.class,
        SecurityConfig.class,
        AddressDtoQueryParamsExtractor.class
})
public class ComplytSalesTaxRatesRouterTest {

    @Autowired
    ComplytSalesTaxRatesRouter addressWithSalesTaxRatesRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ComplytSalesTaxRatesFacade addressWithSalesTaxRatesFacade;

    @Test
    @WithMockUser
    public void findByAddress_AddressWithSalesTaxRatesFound_Returns200() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();
        ComplytSalesTaxRates addressWithSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(addressWithSalesTaxRatesFacade.findByAddress(address)).thenReturn(Mono.just(addressWithSalesTaxRates));
        ComplytSalesTaxRatesDto addressWithSalesTaxRatesDto = ComplytSalesTaxRatesMapper.INSTANCE
                .complytSalesTaxRatesToComplytSalesTaxRates(addressWithSalesTaxRates);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytSalesTaxRatesDto.class)
                .value(addressWithSalesTaxRatesItem -> addressWithSalesTaxRatesItem, equalTo(addressWithSalesTaxRatesDto));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithSalesTaxRatesWithRatesMetaDataFound_Returns200() {
        // Given
        RatesMetaData ratesMetaData = new RatesMetaData(new BigDecimal("0.01"), new BigDecimal("0.01"));
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();
        ComplytSalesTaxRates addressWithSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates()
                .withSalesTaxRates(TestUtilities.createCaliforniaSalesTaxRates().withRatesMetaData(ratesMetaData));
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(addressWithSalesTaxRatesFacade.findByAddress(address)).thenReturn(Mono.just(addressWithSalesTaxRates));
        ComplytSalesTaxRatesDto addressWithSalesTaxRatesDto = ComplytSalesTaxRatesMapper.INSTANCE
                .complytSalesTaxRatesToComplytSalesTaxRates(addressWithSalesTaxRates);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytSalesTaxRatesDto.class)
                .value(addressWithSalesTaxRatesItem -> addressWithSalesTaxRatesItem, equalTo(addressWithSalesTaxRatesDto));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithSalesTaxRatesNotFound_Throws404() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(addressWithSalesTaxRatesFacade.findByAddress(address)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithBlankCity_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withCity("");

        Set<String> expectedErrors = Set.of(
                "Address.city " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("isPartial", false)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));

    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithNullCity_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();

        Set<String> expectedErrors = Set.of(
                "Address.city " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));

    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithBlankState_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withState("");

        Set<String> expectedErrors = Set.of(
                "Address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));

    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithNullState_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();

        Set<String> expectedErrors = Set.of(
                "Address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithBlankStreet_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withStreet("");

        Set<String> expectedErrors = Set.of(
                "Address.street " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));

    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithNullStreet_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();

        Set<String> expectedErrors = Set.of(
                "Address.street " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithBlankZip_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withZip("");

        Set<String> expectedErrors = Set.of(
                "Address.zip " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));

    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithNullZip_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();

        Set<String> expectedErrors = Set.of(
                "Address.zip " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithBlankCountry_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withCountry("");

        Set<String> expectedErrors = Set.of(
                "Address.country " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithNullCountry_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();

        Set<String> expectedErrors = Set.of(
                "Address.country " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_InternalServerError_Returns500() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();
        Address address = TestUtilities.createAddressInCalifornia();

        // When
        when(addressWithSalesTaxRatesFacade.findByAddress(address)).thenThrow(RuntimeException.class);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @WithMockUser
    public void getAny_InvalidUrl_Returns404() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(ComplytSalesTaxRatesRouter.BASE_URL + "/resource_not_found").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void get_UnauthenticatedUser_Returns401() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(ComplytSalesTaxRatesRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @WithMockUser
    public void findByAddress_LengthGreaterThen50country_Returns400ValidationError() {
        // Given
        String countryWithLength51 = TestUtilities.stringWithLength(51);

        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withCountry(countryWithLength51);
        Set<String> expectedErrors = Set.of("Address.country " + StringErrorMessages.MAX_50_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_LengthGreaterThen100City_Returns400ValidationError() {
        // Given
        String cityWithLength51 = TestUtilities.stringWithLength(101);
        ;

        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withCity(cityWithLength51);
        Set<String> expectedErrors = Set.of("Address.city " + StringErrorMessages.MAX_100_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_LengthGreaterThen100State_Returns400ValidationError() {
        // Given
        String stateWithLength101 = TestUtilities.stringWithLength(101);

        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withState(stateWithLength101);
        Set<String> expectedErrors = Set.of("Address.state " + StringErrorMessages.MAX_100_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_LengthGreaterThen100County_Returns400ValidationError() {
        // Given
        String countyWithLength101 = TestUtilities.stringWithLength(101);

        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withCounty(countyWithLength101);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.county " + StringErrorMessages.MAX_100_ERROR));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .queryParam("county", addressDto.county())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_LengthGreaterThen200Street_Returns400ValidationError() {
        // Given
        String streetWithLength101 = TestUtilities.stringWithLength(201);

        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withStreet(streetWithLength101);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.street " + StringErrorMessages.MAX_200_ERROR));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .queryParam("county", addressDto.county())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_LengthGreaterThen20Zip_Returns400ValidationError() {
        // Given
        String zipWithLength101 = TestUtilities.stringWithLength(21);

        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withZip(zipWithLength101);
        Set<String> expectedErrors = Set.of("Address.zip " + StringErrorMessages.MAX_20_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Test
    public void findByAddress_NullHandler_ThrowsNullPointerException() {
        // Given
        ComplytSalesTaxRatesHandler nullComplytSalesTaxRatesHandler = null;
        ComplytSalesTaxRatesRouter complytSalesTaxRates = new ComplytSalesTaxRatesRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            complytSalesTaxRates.getComplytSalesTaxRatesByAddress(nullComplytSalesTaxRatesHandler);
        });

        // Then
        assertEquals("complytSalesTaxRatesHandler " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE, exception.getMessage());
    }

}
