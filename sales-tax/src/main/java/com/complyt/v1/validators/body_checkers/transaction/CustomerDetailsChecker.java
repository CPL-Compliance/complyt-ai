package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.utils.StringChecker;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import reactor.core.publisher.Flux;

public class CustomerDetailsChecker implements DtoBodyChecker<TransactionDto> {
    @Override
    public Flux<String> check(TransactionDto transactionDto) {
        return transactionDto.customerId() != null ||
                StringChecker.isInputValid(transactionDto.customerExternalRef(),
                        transactionDto.customerSource()) ? Flux.empty() : Flux.just(DtoErrorMessages.CUSTOMER_MISSING_ID_OR_EXTERNAL_REFERENCE_AND_SOURCE);
    }
}
