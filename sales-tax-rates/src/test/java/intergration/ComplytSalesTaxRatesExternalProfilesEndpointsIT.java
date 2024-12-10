package intergration;

import com.complyt.SalesTaxRatesApplication;
import com.complyt.business.sales_tax_web_clients.StubFastTaxWebClientWrapper;
import com.complyt.config.SecurityConfig;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.RatesMetaData;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.common_sales_tax_rates.CommonAddressDto;
import com.complyt.v1.model.common_sales_tax_rates.CommonSalesTaxRatesDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import com.complyt.v1.router.ComplytSalesTaxRatesRouter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxRatesApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
@ActiveProfiles({"stubFastTax"})
public class ComplytSalesTaxRatesExternalProfilesEndpointsIT extends MongoContainerInitializerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    LocalDateTime requestedTime;

    @BeforeEach
    void setUp() {
        requestedTime = LocalDateTime.now();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findByAddress_FirstAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto();
        AddressDto addressWithCounty = stubFastTaxAddress.withCounty("Arapahoe").withStreet("street");
        SalesTaxRatesDto stubFastTaxSalesTaxRates = TestUtilities.createStubFastTaxSalesTaxRatesDto();
        CommonAddressDto returnedAddress = TestUtilities.createCommonAddressDto(addressWithCounty);

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
                        .queryParam("isPartial", stubFastTaxAddress.isPartial())
                        .queryParam("requiredDate", requestedTime)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommonSalesTaxRatesDto.class)
                .value(commonSalesTaxRatesDto -> {
                    assertEquals(returnedAddress, commonSalesTaxRatesDto.address());
                    assertEquals(stubFastTaxSalesTaxRates, commonSalesTaxRatesDto.salesTaxRates());
                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findAddress_RateFoundInDB_Return200() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto().withState("Hawaii").withZip("99501");
        AddressDto validatedAddress = stubFastTaxAddress.withCounty("Anchorage").withStreet("751-2696 205 E Benson Blvd").withCity("Anchorage"); // Validated by Here
        CommonAddressDto returnedAddress = TestUtilities.createCommonAddressDto(validatedAddress);
        SalesTaxRatesDto stubFastTaxSalesTaxRates = new SalesTaxRatesDto(new BigDecimal("0.0625"), BigDecimal.ZERO, new BigDecimal("0.01"), new BigDecimal("0.01"), new RatesMetaData(BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("0.01")), null, null, null, new BigDecimal("0.0825"));

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
                        .queryParam("requiredDate", requestedTime)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CommonSalesTaxRatesDto.class)
                .value(commonSalesTaxRatesDto -> {
                    assertEquals(returnedAddress, commonSalesTaxRatesDto.address());
                    assertEquals(stubFastTaxSalesTaxRates,
                            commonSalesTaxRatesDto.salesTaxRates());
                });
    }

    @Order(4)
    @Test
    // This Test is for retrieving existing rates object from DB
    // There is no getAll endpoint for checking if it exists but there were tests to it, and it has been validated
    public void findAll_FindsAllInsertedComplytSalesTaxRates_ChecksCount() {
        Flux<ComplytSalesTaxRates> complytSalesTaxRatesFlux = reactiveMongoTemplate.findAll(ComplytSalesTaxRates.class, "colorado");

        StepVerifier.create(complytSalesTaxRatesFlux).expectNextCount(1).verifyComplete();
    }

}