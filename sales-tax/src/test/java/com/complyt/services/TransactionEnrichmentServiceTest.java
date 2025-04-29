package com.complyt.services;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.exceptions.types.CustomerNotFoundApiException;
import com.complyt.v1.mappers.transaction.TransactionMapper;
import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionEnrichmentServiceTest {
    @Mock
    Customer customer;
    @Mock
    CustomerService customerService;
    @InjectMocks
    TransactionEnrichmentService transactionEnrichmentService;

    Transaction transaction;
    TransactionDto transactionDto;
    UUID customerComplytId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        UnitTestUtilities unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenantId");
        transaction = unitTestUtilities.createTransaction("transactionId");
        transactionDto= unitTestUtilities.createTransactionDto("transactionId");
    }

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDIsNotNull_shouldCallFindByComplytId_andReturnCustomer() {
        when(customer.getComplytId()).thenReturn(customerComplytId);
        when(customerService.findByComplytId(any(UUID.class))).thenReturn(Mono.just(customer));
        Transaction expected = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto)
                .withCustomer(customer).withCustomerId(customerComplytId);

        Mono<Transaction> result = transactionEnrichmentService.enrich(transactionDto);
        verify(customerService).findByComplytId(any());
        StepVerifier.create(result).expectNext(expected).verifyComplete();
    }

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDAndExternalReferenceAndCustomerSourceNull_returnError() {
        Mono<Transaction> result = transactionEnrichmentService.enrich(transactionDto
                .withCustomerId(null).withCustomerSource(null).withCustomerExternalRef(null));

        StepVerifier.create(result).expectError(CustomerNotFoundApiException.class).verify();
    }

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDAndExternalReferenceAreNull_returnError() {
        Mono<Transaction> result = transactionEnrichmentService.enrich(transactionDto
                .withCustomerId(null).withCustomerSource("source").withCustomerExternalRef(null));

        StepVerifier.create(result).expectError(CustomerNotFoundApiException.class).verify();
    }

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDAndCustomerSourceAreNull_returnError() {
        Mono<Transaction> result = transactionEnrichmentService.enrich(transactionDto.withCustomerId(null).withCustomerExternalRef("externalRef"));

        StepVerifier.create(result).expectError(CustomerNotFoundApiException.class).verify();
    }


    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDIsNull_andCustomerExternalReferenceAndSourceValid_shouldCallFindByExternalReferenceAndSource_andReturnCustomer() {
        when(customerService.findByExternalIdAndSource(anyString(), anyString())).thenReturn(Mono.just(customer));
        when(customer.getComplytId()).thenReturn(customerComplytId);
        Transaction expected = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto)
                .withCustomer(customer).withCustomerId(customerComplytId);

        Mono<Transaction> result = transactionEnrichmentService.enrich(transactionDto.withCustomerId(null).withCustomerExternalRef("externalRef")
                .withCustomerSource("source"));
        verify(customerService).findByExternalIdAndSource(any(), any());
        StepVerifier.create(result).expectNext(expected).verifyComplete();
    }

    @Test
    void whenDeterminingCustomer_allInputsValid_shouldPreferComplytId_andshouldCallFindByComplytId_andReturnCustomer() {
        when(customer.getComplytId()).thenReturn(customerComplytId);
        when(customerService.findByComplytId(any())).thenReturn(Mono.just(customer));
        Transaction expected = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto)
                .withCustomer(customer).withCustomerId(customerComplytId);

        Mono<Transaction> result = transactionEnrichmentService.enrich(transactionDto);
        verify(customerService).findByComplytId(any());
        StepVerifier.create(result).expectNext(expected).verifyComplete();
    }
}