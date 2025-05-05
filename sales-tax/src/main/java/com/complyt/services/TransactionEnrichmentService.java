package com.complyt.services;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.StringChecker;
import com.complyt.v1.exceptions.types.CustomerNotFoundApiException;
import com.complyt.v1.mappers.transaction.TransactionMapper;
import com.complyt.v1.models.transaction.TransactionDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class TransactionEnrichmentService {

    final CustomerService customerService;

    public Mono<Transaction> enrich(final TransactionDto transactionDto) {
        return
                determineCustomerForTransaction(
                        transactionDto.customerId(),
                        transactionDto.customerExternalId(),
                        transactionDto.customerSource()
                )
                        .map(customer -> mapTransactionWithCustomerDetails(transactionDto, customer));
    }

    private Mono<Customer> determineCustomerForTransaction(final UUID customerId, final String customerExternalId,
                                                           final String customerSource) {
        Mono<Customer> customerMono = Mono.empty();
        if (customerId != null) {
            customerMono = customerService.findByComplytId(customerId);
        } else if (StringChecker.isInputValid(customerExternalId, customerSource)) {
            customerMono = customerService.findByExternalIdAndSource(customerExternalId, customerSource);
        }
        return customerMono.switchIfEmpty(Mono.error(CustomerNotFoundApiException::new));
    }

    private Transaction mapTransactionWithCustomerDetails(final TransactionDto transactionDto,
                                                          final Customer customer){
        return TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto)
        .setCustomer(customer)
                .setCustomerId(customer.getComplytId());
    }
}
