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

import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class SimpleIntegrationTestIT extends MongoContainerInitializer {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    TenantResolver tenantResolver;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("mydatabase"));
    }

    @Test
    public void simpleIntegrationTest() {
        System.out.println("This is an integration test");
    }

    @Test
    void isContainerUpIT() {
        LOGGER.info("containerName: " + MONGO_CONTAINER.getContainerName());
        assertEquals("mongo:latest", MONGO_CONTAINER.getDockerImageName());
    }

    @WithMockUser
    @Test void getAllTransaction_ReturnsAllTransactionIT() {

        when(tenantResolver.resolve()).thenReturn(Mono.just( "org_SttAcBkK7b32w7kA"));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> LOGGER.info("All transactions: " + transactionDtos.toString()));
    }
}
