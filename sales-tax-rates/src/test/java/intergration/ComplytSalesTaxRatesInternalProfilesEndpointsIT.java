package intergration;

import com.complyt.SalesTaxRatesApplication;
import com.complyt.business.internal_sales_tax_rates.InternalRatesCollectionNames;
import com.complyt.business.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.config.SecurityConfig;
import com.complyt.domain.Address;
import com.complyt.domain.RatesMetaData;
import com.complyt.domain.enums.RatesStatus;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.complyt.security.TenantResolver;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDataDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import com.complyt.v1.router.ComplytSalesTaxRatesRouter;
import intergration.mongo_validation.ComplytSalesTaxRatesInternalProfilesEndpointsITTemplate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxRatesApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
@ActiveProfiles({"stubInternalRates"})
public class ComplytSalesTaxRatesInternalProfilesEndpointsIT extends MongoContainerInitializerIT implements ComplytSalesTaxRatesInternalProfilesEndpointsITTemplate {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    LocalDateTime requestedTime;

    @Mock
    StubFastTaxWebClientWrapper stubFastTaxWebClientWrapper;

    @MockBean
    TenantResolver tenantResolver;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @BeforeEach
    void setup() {
        Mockito.when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findAddress_InternalRateFound_DateBeforeMaxEffectiveDate_Return200() {
        // Given
        AddressDto stubInternalRatesDto = TestUtilities.createStubInternalTaxAddressDto();
        Address address = new Address(stubInternalRatesDto.city(), stubInternalRatesDto.country(), stubInternalRatesDto.county(), stubInternalRatesDto.state(), stubInternalRatesDto.street(), stubInternalRatesDto.zip(), false);
        requestedTime = LocalDateTime.parse("2021-01-01T00:00:00.000");
        MatchedAddressData matchedAddressData = TestUtilities.createMatchedAddressInCalifornia().withAddress(address);

        SalesTaxRatesDto stubInternalTaxSalesTaxRates = TestUtilities.createStubInternalTax_SalesTaxRatesDto(BigDecimal.valueOf(0.1));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubInternalRatesDto.country())
                        .queryParam("state", stubInternalRatesDto.state())
                        .queryParam("city", stubInternalRatesDto.city())
                        .queryParam("street", stubInternalRatesDto.street())
                        .queryParam("zip", stubInternalRatesDto.zip())
                        .queryParam("effectiveDate", requestedTime)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxRatesDataDto.class)
                .value(salesTaxRatesDataDto -> {
                    assertEquals(matchedAddressData, salesTaxRatesDataDto.matchedAddressData());
                    assertEquals(stubInternalTaxSalesTaxRates, salesTaxRatesDataDto.salesTaxRates());
                    assertNull(salesTaxRatesDataDto.ratesMetaData());
                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findAddress_InternalRateFound_DateBeforeMaxEffectiveDateAndDetailedTrue_Return200() {
        // Given
        AddressDto stubInternalRatesDto = TestUtilities.createStubInternalTaxAddressDto();
        Address address = new Address(stubInternalRatesDto.city(), stubInternalRatesDto.country(), stubInternalRatesDto.county(), stubInternalRatesDto.state(), stubInternalRatesDto.street(), stubInternalRatesDto.zip(), false);
        requestedTime = LocalDateTime.parse("2021-01-01T00:00:00.000");
        MatchedAddressData matchedAddressData = TestUtilities.createMatchedAddressInCalifornia().withAddress(address);

        SalesTaxRatesDto stubInternalTaxSalesTaxRates = TestUtilities.createStubInternalTax_SalesTaxRatesDto(BigDecimal.valueOf(0.1));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubInternalRatesDto.country())
                        .queryParam("state", stubInternalRatesDto.state())
                        .queryParam("city", stubInternalRatesDto.city())
                        .queryParam("street", stubInternalRatesDto.street())
                        .queryParam("zip", stubInternalRatesDto.zip())
                        .queryParam("effectiveDate", requestedTime)
                        .queryParam("detailed", true)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxRatesDataDto.class)
                .value(salesTaxRatesDataDto -> {
                    assertEquals(matchedAddressData, salesTaxRatesDataDto.matchedAddressData());
                    assertEquals(stubInternalTaxSalesTaxRates, salesTaxRatesDataDto.salesTaxRates());
                    assertNotNull(salesTaxRatesDataDto.ratesMetaData());
                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findAddress_InternalRateFound_DateAfterMaxEffectiveDate_Return200() {
        // Given
        AddressDto stubInternalRatesDto = TestUtilities.createStubInternalTaxAddressDto();
        Address address = new Address(stubInternalRatesDto.city(), stubInternalRatesDto.country(), stubInternalRatesDto.county(), stubInternalRatesDto.state(), stubInternalRatesDto.street(), stubInternalRatesDto.zip(), stubInternalRatesDto.isPartial());
        requestedTime = LocalDateTime.parse("2000-01-01T00:00:00.000"); // Before MaxEffectiveDate (2001/01/01)

        SalesTaxRatesDto stubInternalTaxSalesTaxRates = TestUtilities.createStubInternalTax_SalesTaxRatesDto(BigDecimal.valueOf(0.1))
                .withMtaRate(BigDecimal.ZERO).withTaxRate(new BigDecimal("0.2"));
        MatchedAddressData matchedAddressData = TestUtilities.createMatchedAddressInCalifornia().withAddress(address);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubInternalRatesDto.country())
                        .queryParam("state", stubInternalRatesDto.state())
                        .queryParam("city", stubInternalRatesDto.city())
                        .queryParam("street", stubInternalRatesDto.street())
                        .queryParam("zip", stubInternalRatesDto.zip())
                        .queryParam("effectiveDate", requestedTime)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxRatesDataDto.class)
                .value(salesTaxRatesDataDto -> {
                    assertEquals(matchedAddressData, salesTaxRatesDataDto.matchedAddressData());
                    assertEquals(stubInternalTaxSalesTaxRates, salesTaxRatesDataDto.salesTaxRates());
                });
    }



    @Order(1)
    @Test
    @WithMockUser
    public void findAddress_InternalRateNotFound_ExternalRateFoundInDB_Return200() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto().withState("Hawaii").withZip("99501");
        AddressDto validatedAddress = stubFastTaxAddress.withCounty("Anchorage").withStreet("751-2696 205 E Benson Blvd").withCity("Anchorage").withCountry("US"); // Validated by Here
        Address address = new Address(validatedAddress.city(), validatedAddress.country(), validatedAddress.county(), validatedAddress.state(), validatedAddress.street(), validatedAddress.zip(), validatedAddress.isPartial());
        SalesTaxRatesDto stubFastTaxSalesTaxRates = new SalesTaxRatesDto(new BigDecimal("0.0625"), BigDecimal.ZERO, new BigDecimal("0.01"), new BigDecimal("0.01"), new RatesMetaData(BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("0.01")), null, null, null, new BigDecimal("0.0825"));
        MatchedAddressData matchedAddressData = TestUtilities.createMatchedAddressInCalifornia().withAddress(address.withIsPartial(false).withCountry("USA"));

        requestedTime = LocalDateTime.now();

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubFastTaxAddress.country())
                        .queryParam("state", stubFastTaxAddress.state())
                        .queryParam("city", stubFastTaxAddress.city())
                        .queryParam("zip", stubFastTaxAddress.zip())
                        .queryParam("isPartial", stubFastTaxAddress.isPartial())
                        .queryParam("effectiveDate", requestedTime)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxRatesDataDto.class)
                .value(salesTaxRatesDataDto -> {
                    assertEquals(matchedAddressData, salesTaxRatesDataDto.matchedAddressData());
                    assertEquals(stubFastTaxSalesTaxRates, salesTaxRatesDataDto.salesTaxRates());
                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findAddress_InternalRateNotFound_ExternalRateClientWrapper_Return200() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto().withState("West Virginia").withZip("24740-9669").withStreet("751-2696 205 E Benson Blvd");
        AddressDto addressWithCounty = stubFastTaxAddress.withCounty("MERCER");
        Address address = new Address(addressWithCounty.city(), addressWithCounty.country(), addressWithCounty.county(), addressWithCounty.state(), addressWithCounty.street(), addressWithCounty.zip(), addressWithCounty.isPartial());
        SalesTaxRatesDto stubFastTaxSalesTaxRates = TestUtilities.createStubFastTaxSalesTaxRatesDto();
        requestedTime = LocalDateTime.now();
        MatchedAddressData matchedAddressData = TestUtilities.createMatchedAddressInCalifornia().withAddress(address);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubFastTaxAddress.country())
                        .queryParam("state", stubFastTaxAddress.state())
                        .queryParam("city", stubFastTaxAddress.city())
                        .queryParam("street", stubFastTaxAddress.street())
                        .queryParam("zip", stubFastTaxAddress.zip())
                        .queryParam("isPartial", true)
                        .queryParam("effectiveDate", requestedTime)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxRatesDataDto.class)
                .value(salesTaxRatesDataDto -> {
                    assertEquals(matchedAddressData, salesTaxRatesDataDto.matchedAddressData());
                    assertEquals(stubFastTaxSalesTaxRates, salesTaxRatesDataDto.salesTaxRates());
                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findAddress_EffectiveDateNull_Returns200() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto().withState("West Virginia").withZip("24740-9669").withStreet("751-2696 205 E Benson Blvd");

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubFastTaxAddress.country())
                        .queryParam("state", stubFastTaxAddress.state())
                        .queryParam("city", stubFastTaxAddress.city())
                        .queryParam("street", stubFastTaxAddress.street())
                        .queryParam("zip", stubFastTaxAddress.zip())
                        .queryParam("isPartial", true)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxRatesDataDto.class)
                .value(salesTaxRatesDataDto -> {
                    assertEquals(LocalDateTime.now().getYear(), salesTaxRatesDataDto.requestAddress().getEffectiveDate().getYear());
                    assertEquals(LocalDateTime.now().getMonth(), salesTaxRatesDataDto.requestAddress().getEffectiveDate().getMonth());
                    assertEquals(LocalDateTime.now().getDayOfYear(), salesTaxRatesDataDto.requestAddress().getEffectiveDate().getDayOfYear());
                });
    }

    @Order(1)
    @Test
    @Override
    @WithMockUser
    public void update_newInternalRate_Return200() {
        InternalSalesTaxRatesDto internalSalesTaxRatesDto = TestUtilities.createInternalSalesTaxRatesDto();

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("status", RatesStatus.NEW)
                        .build())
                .bodyValue(internalSalesTaxRatesDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InternalSalesTaxRatesDto.class)
                .value(result -> assertEquals(internalSalesTaxRatesDto.salesTaxRates().getTaxRate(), result.salesTaxRates().getTaxRate()));
    }

    @Test
    @Override
    @WithMockUser
    public void update_InsertNewInternalRate_StatusWrong_Return400() {
        InternalSalesTaxRatesDto internalSalesTaxRatesDto = TestUtilities.createInternalSalesTaxRatesDto();

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("status", "None")
                        .build())
                .bodyValue(internalSalesTaxRatesDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Order(2)
    @Test
    @Override
    @WithMockUser
    public void update_updateInternalRate_Return200() {
        String testOldMtaName = "test";
        InternalSalesTaxRatesDto internalSalesTaxRatesDto = TestUtilities.createInternalSalesTaxRatesDto();
        InternalSalesTaxRatesDto updatedInternalRatesDto = internalSalesTaxRatesDto.withInternalSalesTaxRatesMetaData(internalSalesTaxRatesDto.internalSalesTaxRatesMetaData().setMtaName("MTA-TEST"));

        String collectionName = InternalRatesCollectionNames.stateInternalCollectionName(internalSalesTaxRatesDto.address().state());
        int numOfDocumentsInCollection = Objects.requireNonNull(reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, collectionName)
                .collectList().block()).size();
        int numOfDocumentsInCollectionArchived = Objects.requireNonNull(reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME)
                .collectList().block()).size();

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("status", RatesStatus.UPDATE)
                        .build())
                .bodyValue(updatedInternalRatesDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InternalSalesTaxRatesDto.class)
                .value(result -> assertEquals(testOldMtaName, result.internalSalesTaxRatesMetaData().getMtaName())); // returns the old document

        // Collection
        reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, collectionName)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> assertEquals(
                        numOfDocumentsInCollection, // should remain the same
                        list.size(),
                        "Expected collection document count to be " + (numOfDocumentsInCollection) + " but found " + list.size()
                ))
                .verifyComplete();

        // Archived
        reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> assertEquals(
                        numOfDocumentsInCollectionArchived + 1, // should be added
                        list.size(),
                        "Expected archived document count to be " + (numOfDocumentsInCollectionArchived + 1) + " but found " + list.size()
                ))
                .verifyComplete();
    }

    @Test
    @Override
    @WithMockUser
    public void update_updateInternalRate_NotFound_Return404() {
        InternalSalesTaxRatesDto internalSalesTaxRatesDto = TestUtilities.createInternalSalesTaxRatesDto();
        InternalSalesTaxRatesDto updatedInternalRatesDto = internalSalesTaxRatesDto.withAddress(internalSalesTaxRatesDto.address().withZip("11111")); // this zip not in db


        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("status", RatesStatus.UPDATE)
                        .build())
                .bodyValue(updatedInternalRatesDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockUser
    public void update_ArchiveInternalRate_Return200() {
        InternalSalesTaxRatesDto internalSalesTaxRatesDto = TestUtilities.createInternalSalesTaxRatesDto();

        String collectionName = InternalRatesCollectionNames.stateInternalCollectionName(internalSalesTaxRatesDto.address().state());
        int numOfDocumentsInCollection = Objects.requireNonNull(reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, collectionName)
                .collectList().block()).size();

        int numOfDocumentsInCollectionArchived = Objects.requireNonNull(reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME)
                .collectList().block()).size();

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("status", RatesStatus.ARCHIVE)
                        .build())
                .bodyValue(internalSalesTaxRatesDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(InternalSalesTaxRatesDto.class)
                .value(result -> assertEquals(internalSalesTaxRatesDto.salesTaxRates().getTaxRate(), result.salesTaxRates().getTaxRate()));

        // Collection
        reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, collectionName)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> assertEquals(
                        numOfDocumentsInCollection - 1, // should be archived (-1)
                        list.size(),
                        "Expected collection document count to be " + (numOfDocumentsInCollection + 1) + " but found " + list.size()
                ))
                .verifyComplete();

        // Archived
        reactiveMongoTemplate.findAll(InternalSalesTaxRatesDto.class, InternalRatesCollectionNames.ARCHIVED_COLLECTION_NAME)
                .collectList()
                .as(StepVerifier::create)
                .assertNext(list -> assertEquals(
                        numOfDocumentsInCollectionArchived + 1, // should be added (+1)
                        list.size(),
                        "Expected archived document count to be " + (numOfDocumentsInCollectionArchived + 1) + " but found " + list.size()
                ))
                .verifyComplete();
    }
}
