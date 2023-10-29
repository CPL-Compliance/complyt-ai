package intergration;

import com.complyt.SalesTaxRatesApplication;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ComplytSalesTaxRatesDto;
import com.complyt.v1.model.SalesTaxRatesDto;
import com.complyt.v1.router.ComplytSalesTaxRatesRouter;
import com.example.complyt.config.SecurityConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxRatesApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
public class ComplytSalesTaxRatesEndpointsIT extends MongoContainerInitializerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

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
        AddressDto addressWithCounty = stubFastTaxAddress.withCounty("Arapahoe");
        SalesTaxRatesDto stubFastTaxSalesTaxRates = TestUtilities.createStubFastTaxSalesTaxRatesDto();

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
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytSalesTaxRatesDto.class)
                .value(complytSalesTaxRatesDto -> {
                    assertEquals(addressWithCounty, complytSalesTaxRatesDto.address());
                    assertEquals(stubFastTaxSalesTaxRates, complytSalesTaxRatesDto.salesTaxRates());
                });
    }

    @Order(2)
    @Test
    @WithMockUser
    public void findByAddress_SecondAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto();
        AddressDto expectedAddress = stubFastTaxAddress.withStreet("4th Avenue").withCounty("Arapahoe");
        SalesTaxRatesDto stubFastTaxSalesTaxRates = TestUtilities.createStubFastTaxSalesTaxRatesDto();

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubFastTaxAddress.country())
                        .queryParam("state", expectedAddress.state())
                        .queryParam("city", stubFastTaxAddress.city())
                        .queryParam("street", expectedAddress.street())
                        .queryParam("zip", stubFastTaxAddress.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytSalesTaxRatesDto.class)
                .value(addressWithSalesTaxRatesDto -> {
                    assertEquals(addressWithSalesTaxRatesDto.address(), expectedAddress);
                    assertEquals(addressWithSalesTaxRatesDto.salesTaxRates(), stubFastTaxSalesTaxRates);
                });
    }

    @Order(3)
    @Test
    @WithMockUser
    public void findByAddress_ThirdAddressToInsert_InsertsNewComplytSalesTaxRatesAndReturnsIt() {
        // Given
        SalesTaxRatesDto stubFastTaxSalesTaxRates = TestUtilities.createStubFastTaxSalesTaxRatesDto();
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto();

        AddressDto expectedAddress = stubFastTaxAddress.withStreet("5th Avenue").withCounty("Arapahoe");

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ComplytSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubFastTaxAddress.country())
                        .queryParam("state", expectedAddress.state())
                        .queryParam("city", stubFastTaxAddress.city())
                        .queryParam("street", expectedAddress.street())
                        .queryParam("zip", stubFastTaxAddress.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytSalesTaxRatesDto.class)
                .value(complytSalesTaxRatesDto -> {
                    assertEquals(complytSalesTaxRatesDto.address(), expectedAddress);
                    assertEquals(complytSalesTaxRatesDto.salesTaxRates(), stubFastTaxSalesTaxRates);
                });
    }

    @Order(4)
    @Test
    // This Test is for retrieving existing rates object from DB
    // There is no getAll endpoint for checking if it exists but there were tests to it, and it has been validated
    public void findAll_FindsAllInsertedComplytSalesTaxRates_ChecksCount() {
        Flux<ComplytSalesTaxRates> complytSalesTaxRatesFlux = reactiveMongoTemplate.findAll(ComplytSalesTaxRates.class, "colorado");

        StepVerifier.create(complytSalesTaxRatesFlux).expectNextCount(3).verifyComplete();
    }

}