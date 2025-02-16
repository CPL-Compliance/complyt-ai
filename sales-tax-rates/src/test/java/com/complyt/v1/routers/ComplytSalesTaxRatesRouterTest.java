package com.complyt.v1.routers;

import com.complyt.config.SecurityConfig;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.SalesTaxRatesData;
import com.complyt.facade.InternalSalesTaxRatesFacade;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handler.ComplytSalesTaxRatesHandler;
import com.complyt.v1.mappers.AddressWithDateMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDataDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDto;
import com.complyt.v1.router.ComplytSalesTaxRatesRouter;
import com.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
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
    ComplytSalesTaxRatesRouter complytSalesTaxRatesRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private InternalSalesTaxRatesFacade internalSalesTaxRatesFacade; //this is not tested really

    private SalesTaxRatesData salesTaxRatesData;

    @BeforeEach
    void setUp() {
        salesTaxRatesData = TestUtilities.createSalesTaxRatesData();
    }

    @Test
    @WithMockUser
    public void findByAddress_CommonRatesFound_Returns200() {
        // Given
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00");
        SalesTaxRatesDto salesTaxRatesDto = TestUtilities.createSalesTaxRatesDto();
        AddressWithDate addressWithDate = AddressWithDateMapper.INSTANCE.addressWithDateDtoToAddressDate(addressWithDateDto);
        salesTaxRatesData = salesTaxRatesData.withRequestAddress(addressWithDate);
        SalesTaxRatesDataDto salesTaxRatesDataDto = new SalesTaxRatesDataDto(null, addressWithDate, TestUtilities.createMatchedAddressInCalifornia(), salesTaxRatesDto, null);

        // When
        when(internalSalesTaxRatesFacade.validateAddress(addressWithDate, false)).thenReturn(Mono.just(salesTaxRatesData));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxRatesDataDto.class)
                .value(responseCommonSalesTaxRatesDto ->
                        assertEquals(salesTaxRatesDataDto, responseCommonSalesTaxRatesDto)
                );
    }


    @Test
    @WithMockUser
    public void findByAddress_AddressWithSalesTaxRatesNotFound_Throws404() {
        // Given
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00");
        AddressWithDate addressWithDate = AddressWithDateMapper.INSTANCE.addressWithDateDtoToAddressDate(addressWithDateDto);

        // When
        when(internalSalesTaxRatesFacade.validateAddress(addressWithDate, false)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithBlankDate_Throws400BadRequest() {
        // Given
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("");

        Set<String> expectedErrors = Set.of(
                "effectiveDate " + DtoErrorMessages.NOT_BLANK_ERROR,
                "effectiveDate " + DtoErrorMessages.DATE_FORMAT_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithNullDate_Throws400BadRequest() {
        // Given
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00");

        Set<String> expectedErrors = Set.of(
                "Address.country " + DtoErrorMessages.NOT_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @WithMockUser
    public void findByAddress_AddressWithBlankCity_Throws400BadRequest() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withCity("");
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);


        Set<String> expectedErrors = Set.of(
                "City " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00");

        Set<String> expectedErrors = Set.of(
                "City " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of(
                "Address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withState("");
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of(
                "Address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of(
                "Street " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withStreet("");
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of(
                "Street " + StringErrorMessages.NOT_BE_BLANK_ERROR + " " + DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of(
                "Address.zip " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00");


        Set<String> expectedErrors = Set.of(
                "Address.zip " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of(
                "Address.country " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00");

        Set<String> expectedErrors = Set.of(
                "Address.country " + StringErrorMessages.NOT_BE_BLANK_ERROR
        );

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00");
        AddressWithDate addressWithDate = TestUtilities.createAddressWithDateInCalifornia(LocalDateTime.now());

        // When
        when(internalSalesTaxRatesFacade.validateAddress(addressWithDate, false)).thenThrow(RuntimeException.class);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of("Address.country " + StringErrorMessages.MAX_50_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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


        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withCity(cityWithLength51);
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of("Address.city " + StringErrorMessages.MAX_100_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of("Address.state " + StringErrorMessages.MAX_100_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = new HashSet<>(List.of("Address.county " + StringErrorMessages.MAX_100_ERROR));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("county", addressWithDateDto.address().county())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        String streetWithLength201 = TestUtilities.stringWithLength(201);


        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withStreet(streetWithLength201);
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = new HashSet<>(List.of("Address.street " + StringErrorMessages.MAX_200_ERROR));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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
        String zipWithLength21 = TestUtilities.stringWithLength(21);

        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia()
                .withZip(zipWithLength21);
        AddressWithDateDto addressWithDateDto = TestUtilities.createAddressWithDateDtoInCalifornia("2011-11-01T00:00:00")
                .withAddress(addressDto);

        Set<String> expectedErrors = Set.of("Address.zip " + StringErrorMessages.MAX_20_ERROR);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", addressWithDateDto.address().country())
                        .queryParam("state", addressWithDateDto.address().state())
                        .queryParam("city", addressWithDateDto.address().city())
                        .queryParam("street", addressWithDateDto.address().street())
                        .queryParam("zip", addressWithDateDto.address().zip())
                        .queryParam("effectiveDate", addressWithDateDto.effectiveDate())
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