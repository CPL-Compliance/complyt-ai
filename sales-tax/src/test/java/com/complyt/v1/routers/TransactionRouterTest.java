package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.domain.Transaction;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.TransactionFacade;
import com.complyt.v1.controllers.TransactionController;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handlers.TransactionHandler;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.models.TransactionDto;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.ValidatorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.core.publisher.Mono;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@WebFluxTest
@WithMockUser(username = "mock", password = "mock")
@ContextConfiguration(classes = {TransactionRouter.class, TransactionHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class TransactionRouterTest {

    Transaction transaction;

    TransactionDto transactionDto;

    @Autowired
    TransactionRouter transactionRouter;

    @MockBean
    private ValidationHandler<TransactionDto, SpringValidatorAdapter> transactionDtoValidationHandler;

    @MockBean
    private TransactionFacade transactionFacade;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        ObjectStub objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString());
        transaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
    }

    @Test
    void getByExternalIdAndSource_FindsTransaction_ReturnsTransaction() {
        // Given
        String externalId = transactionDto.getExternalId();
        String source = transactionDto.getSource();
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transaction));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionItem -> transactionItem, equalTo(transactionDto));
    }





}
