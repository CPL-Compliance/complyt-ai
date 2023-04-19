package intergration;

import com.complyt.SalesTaxRatesApplication;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.AddressWithSalesTaxRatesDto;
import com.complyt.v1.model.SalesTaxRatesDto;
import com.complyt.v1.router.AddressWithSalesTaxRatesRouter;
import com.example.complyt.config.SecurityConfig;
import lombok.NonNull;
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
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxRatesApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
public class AddressWithSalesTaxRatesEndpointsIT extends MongoContainerInitializerIT {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    @NonNull ReactiveMongoTemplate reactiveMongoTemplate;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax_rates"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findByAddress_AddressInCaliforniaAndDoesNotExist_InsertsNewAddressWithSalesTaxRatesAndReturnsIt() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto();
        AddressDto addressWithCounty = stubFastTaxAddress.withCounty("Arapahoe");
        SalesTaxRatesDto stubFastTaxSalesTaxRates = TestUtilities.createStubFastTaxSalesTaxRates();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressWithSalesTaxRatesRouter.BASE_URL)
                        .queryParam("country", stubFastTaxAddress.country())
                        .queryParam("state", stubFastTaxAddress.state())
                        .queryParam("city", stubFastTaxAddress.city())
                        .queryParam("street", stubFastTaxAddress.street())
                        .queryParam("zip", stubFastTaxAddress.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressWithSalesTaxRatesDto.class)
                .value(addressWithSalesTaxRatesDto -> {
                    assertEquals(addressWithSalesTaxRatesDto.address(), addressWithCounty);
                    assertEquals(addressWithSalesTaxRatesDto.salesTaxRates(), stubFastTaxSalesTaxRates);
                });
    }

    @Order(2)
    @Test
    @WithMockUser
    public void findByAddress_AddressInCaliforniaAndExists_ReturnsAddressWithSalesTaxRates() {
        // Given
        AddressDto stubFastTaxAddress = TestUtilities.createStubFastTaxAddressDto();

        AddressDto addressWithCounty = stubFastTaxAddress.withCounty("Arapahoe");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressWithSalesTaxRatesRouter.BASE_URL)
                        .queryParam("county", stubFastTaxAddress.county())
                        .queryParam("country", stubFastTaxAddress.country())
                        .queryParam("state", stubFastTaxAddress.state())
                        .queryParam("city", stubFastTaxAddress.city())
                        .queryParam("street", stubFastTaxAddress.street())
                        .queryParam("zip", stubFastTaxAddress.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressWithSalesTaxRatesDto.class)
                .value(addressWithSalesTaxRatesDto -> assertEquals(addressWithSalesTaxRatesDto.address(), addressWithCounty));
    }

}
