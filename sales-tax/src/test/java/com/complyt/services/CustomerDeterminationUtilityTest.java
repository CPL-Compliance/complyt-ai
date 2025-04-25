package com.complyt.services;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerLookupDetail;
import com.complyt.v1.exceptions.types.CustomerNotFoundApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDeterminationUtilityTest {
    @Mock
    Customer customer;
    @Mock
    CustomerService customerService;
    @InjectMocks
    CustomerDeterminationUtility customerDeterminationUtility;

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDIsNotNull_shouldCallFindByComplytId_andReturnCustomer() {
        when(customerService.findByComplytId(any(UUID.class))).thenReturn(Mono.just(customer));
        CustomerLookupDetail customerLookupDetail = new CustomerLookupDetail(UUID.randomUUID(),"externalRef","");

        Mono<Customer> result = customerDeterminationUtility.determineCustomerForTransaction(customerLookupDetail);
        verify(customerService).findByComplytId(any());
        StepVerifier.create(result).expectNext(customer).verifyComplete();
    }

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDAndExternalReferenceAndCustomerSourceNull_returnError() {
        CustomerLookupDetail customerLookupDetail = new CustomerLookupDetail(null,null,null);

        Mono<Customer> result = customerDeterminationUtility.determineCustomerForTransaction(customerLookupDetail);

        StepVerifier.create(result).expectError(CustomerNotFoundApiException.class).verify();
    }

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDAndExternalReference_returnError() {
        CustomerLookupDetail customerLookupDetail = new CustomerLookupDetail(null,null,"source");

        Mono<Customer> result = customerDeterminationUtility.determineCustomerForTransaction(customerLookupDetail);

        StepVerifier.create(result).expectError(CustomerNotFoundApiException.class).verify();
    }

    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDAndCustomerSource_returnError() {
        CustomerLookupDetail customerLookupDetail = new CustomerLookupDetail(null,"externalRef",null);

        Mono<Customer> result = customerDeterminationUtility.determineCustomerForTransaction(customerLookupDetail);

        StepVerifier.create(result).expectError(CustomerNotFoundApiException.class).verify();
    }


    @Test
    void whenDeterminingCustomer_AndComplytCustomerIDIsNull_andCustomerExternalReferenceAndSourceValid_shouldCallFindByExternalReferenceAndSource_andReturnCustomer() {
        when(customerService.findByExternalIdAndSource(anyString(), anyString())).thenReturn(Mono.just(customer));
        CustomerLookupDetail customerLookupDetail = new CustomerLookupDetail(null,"externalRef","source");

        Mono<Customer> result = customerDeterminationUtility.determineCustomerForTransaction(customerLookupDetail);
        verify(customerService).findByExternalIdAndSource(any(), any());
        StepVerifier.create(result).expectNext(customer).verifyComplete();
    }

    @Test
    void whenDeterminingCustomer_allInputsValid_shouldPreferComplytId_andshouldCallFindByComplytId_andReturnCustomer() {
        when(customerService.findByComplytId(any())).thenReturn(Mono.just(customer));
        CustomerLookupDetail customerLookupDetail = new CustomerLookupDetail(UUID.randomUUID(),"externalRef","source");

        Mono<Customer> result = customerDeterminationUtility.determineCustomerForTransaction(customerLookupDetail);
        verify(customerService).findByComplytId(any());
        StepVerifier.create(result).expectNext(customer).verifyComplete();
    }
}