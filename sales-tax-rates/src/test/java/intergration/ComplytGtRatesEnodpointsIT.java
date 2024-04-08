package intergration;

import com.complyt.SalesTaxRatesApplication;
import com.complyt.v1.model.gt.ComplytGtRatesDto;
import com.complyt.v1.model.gt.GtAddressDto;
import com.complyt.v1.router.GtRatesRouter;
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
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxRatesApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ContextConfiguration(classes = {
        SecurityConfig.class})
public class ComplytGtRatesEnodpointsIT extends MongoContainerInitializerIT {

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
    public void findByAddress_FindsGtAddressWithCountryAndRegion_ReturnsComplytGtRates() {
        // Given
        GtAddressDto canadaGtAddress = TestUtilities.createCanadaGtAddressDto();
        ComplytGtRatesDto expectedComplytGtRatesDto = TestUtilities.createCanadaComplytGtRatesDto();

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GtRatesRouter.BASE_URL)
                        .queryParam("country", canadaGtAddress.country())
                        .queryParam("region", canadaGtAddress.region())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytGtRatesDto.class)
                .value(returnedComplytGtRatesDto -> {
                    assertEquals(returnedComplytGtRatesDto.gtAddress(), expectedComplytGtRatesDto.gtAddress());
                    assertEquals(returnedComplytGtRatesDto.gtRates(), expectedComplytGtRatesDto.gtRates());
                });
    }

    @Order(1)
    @Test
    @WithMockUser
    public void findByAddress_FindsGtAddressWithOnlyCountry_ReturnsComplytGtRates() {
        // Given
        GtAddressDto armeniaGtAddress = TestUtilities.createArmeniaGtAddressDto();
        ComplytGtRatesDto expectedComplytGtRatesDto = TestUtilities.createArmeniaComplytGtRatesDto();

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GtRatesRouter.BASE_URL)
                        .queryParam("country", armeniaGtAddress.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytGtRatesDto.class)
                .value(returnedComplytGtRatesDto -> {
                    assertEquals(returnedComplytGtRatesDto.gtAddress(), expectedComplytGtRatesDto.gtAddress());
                    assertEquals(returnedComplytGtRatesDto.gtRates(), expectedComplytGtRatesDto.gtRates());
                });
    }

}