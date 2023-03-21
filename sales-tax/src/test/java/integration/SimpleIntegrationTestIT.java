package integration;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.routers.TransactionRouter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class SimpleIntegrationTestIT extends MongoContainerInitializer {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @Test
    void isContainerUpIT() {
        LOGGER.info("containerName: " + MONGO_CONTAINER.getContainerName());
        assertEquals(MONGO_IMAGE, MONGO_CONTAINER.getDockerImageName());
    }

    @WithMockUser
    @Test
    void getAllTransaction_ReturnsAllTransactionIT() {

        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> LOGGER.info(transactionDtos.size() + " transactions: " + transactionDtos));
    }
}
