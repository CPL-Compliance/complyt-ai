package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.TransactionFacade;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

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

    String source;

    @BeforeEach
    void setUp() {
        ObjectStub objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transactionDto = objectStub.createTransactionDto(UUID.randomUUID().toString())
                .withInternalTimestamps(null)
                .withExternalTimestamps(null)
                .withCustomer(null);
        transaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
        source = objectStub.getUnifiedSource();
    }

    @Test
    void getByExternalIdAndSource_FindsTransaction_ReturnsTransaction() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
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

    @Test
    void getAll_ExpectTwoTransactions_ReturnsTwoTransactions() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        TransactionDto transactionNoId = transactionDto.withExternalId(firstId);
        TransactionDto secondTransactionNoId = transactionDto.withExternalId(secondId);
        Transaction firstTransaction = transaction.withExternalId(firstId);
        Transaction secondTransaction = transaction.withExternalId(secondId);
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<>() {{
            add(transactionNoId);
            add(secondTransactionNoId);
        }};

        // When
        when(transactionFacade.getAll()).thenReturn(Flux.just(firstTransaction, secondTransaction));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> transactionDtos, equalTo(allTransactionsWithNoId));
    }

    @Test
    void getAllBySource_ExpectTwoTransactions_ReturnsTwoTransactions() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        TransactionDto transactionNoId = transactionDto.withExternalId(firstId);
        TransactionDto secondTransactionNoId = transactionDto.withExternalId(secondId);
        Transaction firstTransaction = transaction.withExternalId(firstId);
        Transaction secondTransaction = transaction.withExternalId(secondId);
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<>() {{
            add(transactionNoId);
            add(secondTransactionNoId);
        }};

        // When
        when(transactionFacade.getAllBySource(source)).thenReturn(Flux.just(firstTransaction, secondTransaction));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> transactionDtos, equalTo(allTransactionsWithNoId));
    }

    @Test
    void getByComplytId_FindsTransaction_ReturnsTransaction() {
        // Given
        UUID complytId = transaction.getComplytId();
        when(transactionFacade.findByComplytId(complytId)).thenReturn(Mono.just(transaction));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionItem -> transactionItem, equalTo(transactionDto));
    }

    @Test
    void upsert_NewTransactionCreated_ReturnsStatus201Created() {
        // Given

        // When + Then
        when(transactionFacade.findByExternalIdAndSource(transactionDto.externalId(), source)).thenReturn(Mono.empty());
        when(transactionFacade.saveTransaction(TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto))).thenReturn(Mono.just(transaction));
        when(transactionDtoValidationHandler.validate(any())).thenReturn(Mono.just(transactionDto));

        TransactionDto expectedTransactionDto = TransactionMapper.INSTANCE.transactionToTransactionDto(transaction);

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + transactionDto.externalId())
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDtoItem -> transactionDtoItem, equalTo(expectedTransactionDto));
    }

    @Test
    void update_TransactionExists_ReturnsStatus200() {
        // Given
        String externalId = transactionDto.externalId();
        Transaction mappedTransaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
        Transaction updatedTransaction = mappedTransaction.withId(transaction.getId());

        // When + Then
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transaction));
        when(transactionFacade.saveTransaction(mappedTransaction)).thenReturn(Mono.empty());
        when(transactionFacade.updateIfModified(externalId, source, mappedTransaction, transaction)).thenReturn(Mono.just(updatedTransaction));
        when(transactionDtoValidationHandler.validate(any())).thenReturn(Mono.just(transactionDto));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(returnedTransaction -> returnedTransaction, equalTo(transactionDto));
    }

    @Test
    void markAsCancelled_CancelsTransaction_TransactionStatusChanges() {
        // Given
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);
        String externalId = transactionDto.externalId();

        // When + Then
        when(transactionFacade.markAsCancelled(externalId, source)).thenReturn(Mono.just(cancelledTransaction));
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getTransactionByExternalIdAndSourceRouterFunction_NullHandler_ThrowsException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getTransactionByExternalIdAndSourceRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    void getAllTransactionsRouterFunction_NullHandler_ThrowsException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getAllTransactionsRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    void getAllTransactionsBySourceRouterFunction_NullHandler_ThrowsException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getAllTransactionsBySourceRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    void getTransactionByComplytIdRouterFunction_NullHandler_ThrowsException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getTransactionByComplytIdRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    void upsertTransactionRouterFunction_NullHandler_ThrowsException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.upsertTransactionRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    void deleteTransactionRouterFunction_NullHandler_ThrowsException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.upsertTransactionRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

}
