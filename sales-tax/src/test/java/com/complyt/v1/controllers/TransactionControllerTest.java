package com.complyt.v1.controllers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.config.JacksonConfig;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.TransactionFacade;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.model.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@Import(JacksonConfig.class)
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebFluxTest(TransactionController.class)
@WithMockUser(username = "mock", password = "mock")
@ContextConfiguration(classes = {TransactionController.class, ApiExceptionConfig.class, GlobalExceptionHandler.class})
class TransactionControllerTest {

    Transaction transactionWithId;

    TransactionDto transactionDto;

    TransactionController transactionController;

    @MockBean
    TransactionFacade transactionFacade;

    @Autowired
    WebTestClient webTestClient;

    String source;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void cleanUp() {
        MockitoAnnotations.openMocks(this);
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        Transaction transactionWithCustomerWithTimestamps = domainObjectStub.createTransaction(UUID.randomUUID().toString())
                .withInternalTimestamps(null).withExternalTimestamps(null);
        transactionWithId = transactionWithCustomerWithTimestamps
                .withCustomer(transactionWithCustomerWithTimestamps.getCustomer()
                        .withInternalTimestamps(null).withExternalTimestamps(null));
        transactionDto = TransactionMapper.INSTANCE.transactionToTransactionDto(transactionWithId);
        source = domainObjectStub.getUnifiedSource();
    }

    @Test
    void initController_NullFacadeInstanceGiven_ThrowsNullPointerException() {
        // Given
        TransactionFacade facade = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> new TransactionController(facade));

        assertEquals("transactionFacade is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsert_NewTransactionCreated_ReturnsStatus201Created() {
        // Given
        Transaction transactionWithSalesTax = transactionWithId.withSalesTax(new SalesTax(0, new SalesTaxRate(0, 0, 0, 0, 0, 0)));

        // When + Then
        when(transactionFacade.findByExternalId(transactionDto.getExternalId(), source)).thenReturn(Mono.empty());
        when(transactionFacade.saveTransaction(TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto))).thenReturn(Mono.just(transactionWithSalesTax));

        TransactionDto expectedTransactionDtoWithSalesTax = TransactionMapper.INSTANCE.transactionToTransactionDto(transactionWithSalesTax);

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/source/" + source + "/externalId/" + transactionDto.getExternalId())
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDtoItem -> transactionDtoItem, equalTo(expectedTransactionDtoWithSalesTax));
    }

    @Test
    void update_TransactionExists_ReturnsStatus200() {
        // Given
        String externalId = transactionDto.getExternalId();
        Transaction mappedTransaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
        Transaction updatedTransaction = mappedTransaction.withId(transactionWithId.getId());

        // When + Then
        when(transactionFacade.findByExternalId(externalId, source)).thenReturn(Mono.just(transactionWithId));
        when(transactionFacade.saveTransaction(mappedTransaction)).thenReturn(Mono.empty());
        when(transactionFacade.updateIfModified(externalId, source, mappedTransaction, transactionWithId)).thenReturn(Mono.just(updatedTransaction));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(returnedTransaction -> returnedTransaction, equalTo(transactionDto));
    }

    @Test
    void getByExternalId_FindsTransaction_ReturnsTransaction() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(transactionFacade.findByExternalId(externalId, source)).thenReturn(Mono.just(transactionWithId));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionItem -> transactionItem, equalTo(transactionDto));
    }

    @Test
    void getByComplytId_FindsTransaction_ReturnsTransaction() {
        // Given
        UUID complytId = transactionWithId.getComplytId();
        when(transactionFacade.findByComplytId(complytId)).thenReturn(Mono.just(transactionWithId));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionItem -> transactionItem, equalTo(transactionDto));
    }

    @Test
    void getByComplytId_DoNotFindTransaction_Returns4xxNotFound() {
        // Given
        UUID complytId = UUID.randomUUID();
        when(transactionFacade.findByComplytId(complytId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getByExternalId_FacadeReturnsMonoEmpty_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(transactionFacade.findByExternalId(externalId, source)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(TransactionController.BASE_URL + "/source/" + source + "/externalId/" + externalId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAll_ExpectTwoTransactions_ReturnsTwoTransactions() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        TransactionDto transactionNoId = transactionDto.withExternalId(firstId);
        TransactionDto secondTransactionNoId = transactionDto.withExternalId(secondId);
        Transaction firstTransaction = transactionWithId.withExternalId(firstId);
        Transaction secondTransaction = transactionWithId.withExternalId(secondId);
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
                        .path(TransactionController.BASE_URL)
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
        Transaction firstTransaction = transactionWithId.withExternalId(firstId);
        Transaction secondTransaction = transactionWithId.withExternalId(secondId);
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
                        .path(TransactionController.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> transactionDtos, equalTo(allTransactionsWithNoId));
    }

    @Test
    void markAsCancelled_CancelsTransaction_TransactionStatusChanges() {
        // Given
        Transaction cancelledTransaction = transactionWithId.withTransactionStatus(TransactionStatus.CANCELLED);

        // When + Then
        when(transactionFacade.markAsCancelled(transactionWithId.getExternalId(), source)).thenReturn(Mono.just(cancelledTransaction));
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/source/" + source + "/externalId/" + transactionWithId.getExternalId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void getByExternalId_NullExternalId_ThrowsNullPointerException() {
        //Given
        String nullExternalId = null;
        TransactionController transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.getByExternalId(nullExternalId, transactionDto.getSource());
        });

        // Then
        assertEquals("externalId is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void getByExternalId_NullSource_ThrowsNullPointerException() {
        //Given
        String nullSource = null;
        TransactionController transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.getByExternalId(transactionDto.getExternalId(), nullSource);
        });

        // Then
        assertEquals("source is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsert_NullExternalId_ThrowsNullPointerException() {
        //Given
        String nullExternalId = null;
        TransactionController transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.upsert(nullExternalId, transactionDto.getSource(), transactionDto);
        });

        // Then
        assertEquals("externalId is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsert_NullSource_ThrowsNullPointerException() {
        //Given
        String nullSource = null;
        TransactionController transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.upsert(transactionDto.getExternalId(), nullSource, transactionDto);
        });

        // Then
        assertEquals("source is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsert_NullTransactionDto_ThrowsNullPointerException() {
        //Given
        TransactionDto nullTransactionDto = null;
        TransactionController transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.upsert(transactionDto.getExternalId(), transactionDto.getSource(), nullTransactionDto);
        });
        // Then
        assertEquals("transactionDto is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void delete_NullExternalIdPassed_ThrowsNullPointerException() {
        //Given
        String nullExternalId = null;
        transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.delete(nullExternalId, transactionDto.getSource());
        });

        // Then
        assertEquals("externalId is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void delete_NullSourcePassed_ThrowsNullPointerException() {
        //Given
        String nullSource = null;
        transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.delete(transactionDto.getExternalId(), nullSource);
        });

        // Then
        assertEquals("source is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void getAllBySource_NullSourcePassed_ThrowsNullPointerException() {
        //Given
        String nullSource = null;
        transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.getAllBySource(nullSource);
        });

        // Then
        assertEquals("source is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void getByComplytId_NullComplytIdPassed_ThrowsNullPointerException() {
        //Given
        UUID complytId = null;
        transactionController = new TransactionController(transactionFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            transactionController.getByComplytId(complytId);
        });

        // Then
        assertEquals("complytId is marked non-null but is null", nullPointerException.getMessage());
    }
}