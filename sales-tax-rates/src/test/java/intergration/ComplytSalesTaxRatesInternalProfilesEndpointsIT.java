package intergration;

import com.complyt.SalesTaxRatesApplication;
import com.complyt.business.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.config.SecurityConfig;
import com.complyt.domain.Address;
import com.complyt.domain.RatesMetaData;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDataDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDto;
import com.complyt.v1.router.ComplytSalesTaxRatesRouter;
import intergration.mongo_validation.ComplytSalesTaxRatesInternalProfilesEndpointsITTemplate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @BeforeEach
    void setUp() {
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
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
}
