package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.vat_validation.ValidatedVatDto;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import com.complyt.v1.routers.VatValidationRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.integration_test.ITUtilities;
import testUtils.annotations.WithMockJwt;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VatValidationEndpointsIT extends TestContainersInitializerIT implements VatValidationEndpointsITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    VatDetailsToValidateDto vatDetailsToValidateDto;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @BeforeEach
    void setup() {
        vatDetailsToValidateDto = ITUtilities.createVatDetailsToValidateDto();
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_ValidateExitingVatDetails_Return200() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("AT", "U28609707");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ValidatedVatDto.class)
                .value(validatedVat -> {
                    assertEquals("AT", validatedVat.countryCode());
                    assertEquals("Austria", validatedVat.countryName());
                    assertEquals("U28609707", validatedVat.vatNumber());
                    assertTrue(validatedVat.valid());
                    assertEquals("Andritz AG", validatedVat.name());
                    assertEquals("Stattegger Straße 18\nAT-8045 Graz", validatedVat.address());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_ValidateExitingVatDetailsCountryCodeIsFullCountryNameAndCodeExistsInVatNumber_Return200() {

        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("Austria", "ATU28609707");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ValidatedVatDto.class)
                .value(validatedVat -> {
                    assertEquals("AT", validatedVat.countryCode());
                    assertEquals("Austria", validatedVat.countryName());
                    assertEquals("U28609707", validatedVat.vatNumber());
                    assertTrue(validatedVat.valid());
                    assertEquals("Andritz AG", validatedVat.name());
                    assertEquals("Stattegger Straße 18\nAT-8045 Graz", validatedVat.address());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_ValidateNewVatDetails_Return201() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("BE", "0835221567");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ValidatedVatDto.class)
                .value(validatedVat -> {
                    assertEquals("BE", validatedVat.countryCode());
                    assertEquals("Belgium", validatedVat.countryName());
                    assertEquals("0835221567", validatedVat.vatNumber());
                    assertTrue(validatedVat.valid());
                    assertEquals("BV BE³-PROJECTS", validatedVat.name());
                    assertEquals("Kasteeldreef 9\\n2940 Stabroek", validatedVat.address());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockJwt
    public void upsert_ValidateAlreadyExistingVatDetails_Return200() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("BE", "0835221567");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(ValidatedVatDto.class)
                .value(validatedVat -> {
                    assertEquals("BE", validatedVat.countryCode());
                    assertEquals("Belgium", validatedVat.countryName());
                    assertEquals("0835221567", validatedVat.vatNumber());
                    assertTrue(validatedVat.valid());
                    assertEquals("BV BE³-PROJECTS", validatedVat.name());
                    assertEquals("Kasteeldreef 9\\n2940 Stabroek", validatedVat.address());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_ValidateVatDetailsNotValid_Return201WithSameValuesAsInputAndValidFalse() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("FR", "INVALID123");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ValidatedVatDto.class)
                .value(validatedVat -> {
                    assertEquals("FR", validatedVat.countryCode());
                    assertEquals("INVALID123", validatedVat.vatNumber());
                    assertFalse(validatedVat.valid());
                    assertNull(validatedVat.name());
                    assertNull(validatedVat.address());
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_CountryCodeIsBlank_Return400() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("", "0835221567");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("countryCode parameter " + GenericErrorMessages.MIN_1_MAX_50_ERROR));
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_CountryCodeIsNull_Return400() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto(null, "0835221567");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
//                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("countryCode " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR));
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_CountryCodeIsMoreThan50Characters_Return400() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("A".repeat(51), "0835221567");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("countryCode parameter " + GenericErrorMessages.MIN_1_MAX_50_ERROR));
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_VatNumberIsBlank_Return400() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("BE", "");

        // When + Then
        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("vatNumber parameter " + GenericErrorMessages.MIN_1_MAX_20_ERROR));
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_VatNumberIsNull_Return400() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("BE", null);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
//                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("vatNumber " + DtoErrorMessages.NOT_NULL_OR_BLANK_ERROR));
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_VatNumberIsMoreThan20Characters_Return400() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("BE", "1".repeat(21));

        // When + Then
        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("vatNumber parameter " + GenericErrorMessages.MIN_1_MAX_20_ERROR));
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void upsert_ErrorInValidationWebClient_Return400BadCountry() {
        // Given
        VatDetailsToValidateDto vatDetails = new VatDetailsToValidateDto("Error", "0835221567");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(VatValidationRouter.BASE_URL + "/validate")
                        .queryParam("countryCode", vatDetails.countryCode())
                        .queryParam("vatNumber", vatDetails.vatNumber())
                        .build())
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(vatDetails.countryCode() + ": " + DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR));
                });
    }
}
