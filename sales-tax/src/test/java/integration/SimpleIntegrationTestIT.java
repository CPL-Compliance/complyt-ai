package integration;

import com.complyt.SalesTaxApplication;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
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
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
public class SimpleIntegrationTestIT extends MongoContainerInitializer {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;
    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
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
    @Test
    void getAllTransactionDirectlyAndCustomer_ReturnsAllTransactionIT() {

        Flux<Transaction> transactionFlux = reactiveMongoTemplate.findAll(Transaction.class).map(transaction -> {
            LOGGER.info("test!!! " + transaction);
            return transaction;
        });

        Flux<Customer> customerFlux = reactiveMongoTemplate.findAll(Customer.class).map(customer -> {
            LOGGER.info("test!!! " + customer);
            return customer;
        });

        StepVerifier.create(transactionFlux).expectNextCount(1).verifyComplete();
        StepVerifier.create(customerFlux).expectNextCount(2).verifyComplete();
    }

    @WithMockUser
    @Test
    void getAllTransaction_ReturnsAllTransactionIT() {

        when(tenantResolver.resolve()).thenReturn(Mono.just("org_SttAcBkK7b32w7kA"));

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
