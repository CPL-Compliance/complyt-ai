package com.complyt.v1.controllers;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.facades.TransactionFacade;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.model.TransactionDto;
import org.bson.types.ObjectId;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(TransactionController.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
class TransactionControllerTest {

    @MockBean
    private TransactionFacade transactionFacade;

    @Autowired
    private WebTestClient webTestClient;

    Transaction transactionWithId;

    TransactionDto transactionDto;

    @BeforeEach
    void cleanUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        ObjectId clientId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };

        transactionWithId = Transaction.builder()
                .id(id)
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .customerId(customerId)
                .transactionStatus(TransactionStatus.ACTIVE)
                .clientId(clientId)
                .build();

        transactionDto = TransactionMapper.INSTANCE.transactionToTransactionDto(transactionWithId);
    }

    @WithUserDetails()
    @Test
    void initController_NullFacadeInstanceGiven_ThrowsNullPointerException() {
        // Given
        TransactionFacade facade = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> new TransactionController(facade));

        assertEquals(nullPointerException.getMessage(), "transactionFacade is marked non-null but is null");
    }

    @WithUserDetails()
    @Test
    void upsert_NewTransactionCreated_ReturnsStatus201Created() {
        // Given
        Transaction transactionWithSalesTax = transactionWithId.withSalesTax(new SalesTax(0, new SalesTaxRate(0, 0, 0, 0, 0, 0)));

        // When + Then
        when(transactionFacade.findByExternalId(transactionDto.getExternalId())).thenReturn(Mono.empty());
        when(transactionFacade.saveTransaction(TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto))).thenReturn(Mono.just(transactionWithSalesTax));

        TransactionDto transactionDtoWithSalesTax = TransactionMapper.INSTANCE.transactionToTransactionDto(transactionWithSalesTax);

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/" + transactionDto.getExternalId())
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDtoItem -> transactionDtoItem, equalTo(transactionDtoWithSalesTax));
    }

    @WithUserDetails()
    @Test
    void update_TransactionExists_ReturnsStatus200() {
        // Given
        String externalId = transactionDto.getExternalId();
        Transaction mappedTransaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
        Transaction updatedTransaction = mappedTransaction.withId(transactionWithId.getId());

        // When + Then
        when(transactionFacade.findByExternalId(externalId)).thenReturn(Mono.just(transactionWithId));
        when(transactionFacade.saveTransaction(mappedTransaction)).thenReturn(Mono.empty());
        when(transactionFacade.updateIfModified(externalId, mappedTransaction, transactionWithId)).thenReturn(Mono.just(updatedTransaction));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(returnedTransaction -> returnedTransaction, equalTo(transactionDto));
    }

    @WithUserDetails()
    @Test
    void getOne_FindsTransaction_ReturnsTransaction() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(transactionFacade.findByExternalId(externalId)).thenReturn(Mono.just(transactionWithId));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionItem -> transactionItem, equalTo(transactionDto));
    }

    @WithUserDetails()
    @Test
    void getOne_OperationFails_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(transactionFacade.findByExternalId(externalId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @WithUserDetails()
    @Test
    void getAll_ExpectTwoTransactions_ReturnsTwoTransactions() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        TransactionDto transactionNoId = transactionDto.withExternalId(firstId);
        TransactionDto secondTransactionNoId = transactionDto.withExternalId(secondId);
        Transaction firstTransaction = transactionWithId.withExternalId(firstId);
        Transaction secondTransaction = transactionWithId.withExternalId(secondId);
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<TransactionDto>() {{
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

    @WithUserDetails()
    @Test
    void markAsCancelled_CancelsTransaction_TransactionStatusChanges() {
        // Given
        Transaction cancelledTransaction = transactionWithId.withTransactionStatus(TransactionStatus.CANCELLED);

        // When + Then
        when(transactionFacade.markAsCancelled(transactionWithId.getExternalId())).thenReturn(Mono.just(cancelledTransaction));
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionController.BASE_URL + "/" + transactionWithId.getExternalId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }
}