package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerDetailsCheckerTest {
    CustomerDetailsChecker customerDetailsChecker = new CustomerDetailsChecker();

    @Mock
    TransactionDto transactionDto;

    @Test
    void check_customerId_notNull_returnEmpty() {
        when(transactionDto.customerId()).thenReturn(UUID.randomUUID());

        Flux<String> result = customerDetailsChecker.check(transactionDto);
        StepVerifier.create(result).expectNextCount(0).verifyComplete();
    }

    @Test
    void check_customerIdIsNull_andCustomerExternalIdAndCustomerSourceValid_returnEmpty() {
        when(transactionDto.customerId()).thenReturn(null);
        when(transactionDto.customerExternalId()).thenReturn("externalReference");
        when(transactionDto.customerSource()).thenReturn("source");

        Flux<String> result = customerDetailsChecker.check(transactionDto);
        StepVerifier.create(result).expectNextCount(0).verifyComplete();
    }

    @Test
    void check_customerIdAndCustomerExternalIdAndCustomerSource_Null_returnFluxError() {
        when(transactionDto.customerId()).thenReturn(null);
        when(transactionDto.customerExternalId()).thenReturn(null);
        when(transactionDto.customerSource()).thenReturn(null);

        Flux<String> result = customerDetailsChecker.check(transactionDto);
        StepVerifier.create(result).expectNext(DtoErrorMessages.CUSTOMER_MISSING_ID_OR_EXTERNAL_REFERENCE_AND_SOURCE).verifyComplete();
    }

    @Test
    void check_customerIdAndCustomerExternalId_Null_returnFluxError() {
        when(transactionDto.customerId()).thenReturn(null);
        when(transactionDto.customerExternalId()).thenReturn(null);
        when(transactionDto.customerSource()).thenReturn("randomSource");

        Flux<String> result = customerDetailsChecker.check(transactionDto);
        StepVerifier.create(result).expectNext(DtoErrorMessages.CUSTOMER_MISSING_ID_OR_EXTERNAL_REFERENCE_AND_SOURCE).verifyComplete();
    }


    @Test
    void check_customerIdAndCustomerSource_Null_returnFluxError() {
        when(transactionDto.customerId()).thenReturn(null);
        when(transactionDto.customerExternalId()).thenReturn("randomExternalReference");
        when(transactionDto.customerSource()).thenReturn(null);

        Flux<String> result = customerDetailsChecker.check(transactionDto);
        StepVerifier.create(result).expectNext(DtoErrorMessages.CUSTOMER_MISSING_ID_OR_EXTERNAL_REFERENCE_AND_SOURCE).verifyComplete();
    }
}
