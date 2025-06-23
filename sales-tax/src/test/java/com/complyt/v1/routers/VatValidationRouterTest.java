package com.complyt.v1.routers;

import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.facades.VatValidationFacade;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handlers.VatValidationHandler;
import com.complyt.v1.models.vat_validation.ValidatedVatDto;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.annotations.WithMockJwt;
import testUtils.unit_test.UnitTestUtilities;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebFluxTest
@ContextConfiguration(classes = {VatValidationRouter.class, VatValidationHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class VatValidationRouterTest implements VatValidationRouterTestTemplate {

    @Autowired
    VatValidationRouter vatValidationRouter;

    @MockBean
    VatValidationFacade vatValidationFacade;

    @Autowired
    private WebTestClient webTestClient;

    UnitTestUtilities testUtilities;
    VatDetailsToValidateDto vatDetailsToValidateDto;
    ValidatedVatDto validatedVatDto;

    VatDetailsToValidate vatDetailsToValidate;
    ValidatedVat validatedVat;
    LocalDateTime localDateTime;

    static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
            when(TenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));

        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        webTestClient = webTestClient.mutate().responseTimeout(Duration.ofSeconds(10)).build();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        localDateTime = LocalDateTime.now();

        vatDetailsToValidateDto = testUtilities.createVatDetailsToValidateDto();
        validatedVatDto = testUtilities.createValidatedVatDto(localDateTime, localDateTime);

        vatDetailsToValidate = testUtilities.createVatDetailsToValidate();
        validatedVat = testUtilities.createValidatedVat(localDateTime, localDateTime);
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_ValidateNewVatDetails_Return201() {
        // Given
        ValidatedVatDto expectedValidatedVat = testUtilities.createValidatedVatDto(localDateTime, localDateTime);

        when(vatValidationFacade.findValidatedVat(vatDetailsToValidate)).thenReturn(Mono.empty());
        when(vatValidationFacade.validateVat(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetailsToValidateDto.countryCode())
                        .queryParam("vatNumber", vatDetailsToValidateDto.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ValidatedVatDto.class)
                .value(resultValidatedVat -> assertEquals(expectedValidatedVat, resultValidatedVat));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_ValidateAlreadyExistingVatDetails_Return200() {
        // Given
        ValidatedVatDto expectedValidatedVat = testUtilities.createValidatedVatDto(localDateTime, localDateTime);

        // When
        when(vatValidationFacade.findValidatedVat(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat));

        // we need to mock because we run the requests simultaneously
        when(vatValidationFacade.validateVat(vatDetailsToValidate)).thenReturn(Mono.just(validatedVat.withName("different-name-to-prof-it's-not-coming-from-here")));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetailsToValidateDto.countryCode())
                        .queryParam("vatNumber", vatDetailsToValidateDto.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ValidatedVatDto.class)
                .value(resultValidatedVat -> assertEquals(expectedValidatedVat, resultValidatedVat));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_ValidateVatDetailsNotValid_Return400CountryNotValid() {
        // Given
        VatDetailsToValidateDto brokenVatDetailsDto = testUtilities.createVatDetailsToValidateDto()
                .withVatNumber("12345")
                .withCountryCode("non-existing-country");

        HashSet<String> expectedErrors = new HashSet<>(List.of(
                brokenVatDetailsDto.countryCode() + ": " + DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", brokenVatDetailsDto.countryCode())
                        .queryParam("vatNumber", brokenVatDetailsDto.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_VatDetailsCouldNotBeValidatedDueToInternalError_Return500() {
        // When
        when(vatValidationFacade.findValidatedVat(vatDetailsToValidate)).thenReturn(Mono.empty());
        when(vatValidationFacade.validateVat(vatDetailsToValidate)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetailsToValidateDto.countryCode())
                        .queryParam("vatNumber", vatDetailsToValidateDto.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_NullHandler_ThrowsNullPointerException() {
        // Given
        VatValidationHandler nullVatValidationHandler = null;
        vatValidationRouter = new VatValidationRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            vatValidationRouter.getValidatedVat(nullVatValidationHandler);
        });

        // Then
        assertEquals("vatValidationHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_CountryCodeIsBlank_Return400() {
        // Given
        VatDetailsToValidateDto problematicVatValidationDetails = vatDetailsToValidateDto.withCountryCode("");
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "countryCode parameter " + GenericErrorMessages.MIN_1_MAX_50_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", problematicVatValidationDetails.countryCode())
                        .queryParam("vatNumber", problematicVatValidationDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_CountryCodeIsNull_Return400() {
        // Given
        VatDetailsToValidateDto problematicVatValidationDetails = vatDetailsToValidateDto.withCountryCode(null);
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "countryCode " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("vatNumber", problematicVatValidationDetails.vatNumber())
                        // no countryCode
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_CountryCodeIsMoreThan50Characters_Return400() {
        // Given
        VatDetailsToValidateDto problematicVatValidationDetails = vatDetailsToValidateDto
                .withCountryCode("BE0835221567-111111111111111111111111111111111111111111111111111111111111");
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "countryCode parameter " + GenericErrorMessages.MIN_1_MAX_50_ERROR));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        // no vat number
                        .queryParam("countryCode", problematicVatValidationDetails.countryCode())
                        .queryParam("vatNumber", problematicVatValidationDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_VatNumberIsBlank_Return400() {
        // Given
        VatDetailsToValidateDto problematicVatValidationDetails = vatDetailsToValidateDto.withVatNumber("");
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "vatNumber parameter " + GenericErrorMessages.MIN_1_MAX_20_ERROR));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        // no vat number
                        .queryParam("countryCode", problematicVatValidationDetails.countryCode())
                        .queryParam("vatNumber", problematicVatValidationDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_VatNumberIsNull_Return400() {
        // Given
        VatDetailsToValidateDto problematicVatValidationDetails = vatDetailsToValidateDto.withVatNumber(null);
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "vatNumber " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        // no vat number
                        .queryParam("countryCode", problematicVatValidationDetails.countryCode())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockJwt
    public void upsert_VatNumberIsMoreThan20Characters_Return400() {
        // Given
        VatDetailsToValidateDto problematicVatValidationDetails = vatDetailsToValidateDto
                .withVatNumber("BE0835221567-111111111111111111111111111111111111111111111111111111111111");
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "vatNumber parameter " + GenericErrorMessages.MIN_1_MAX_20_ERROR));




        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("vatNumber", problematicVatValidationDetails.vatNumber())
                        .queryParam("countryCode", problematicVatValidationDetails.countryCode())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }
}
