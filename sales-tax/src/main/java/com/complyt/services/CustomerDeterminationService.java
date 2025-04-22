package com.complyt.services;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerLookupDetail;
import com.complyt.utils.StringChecker;
import com.complyt.v1.exceptions.types.CustomerNotFoundApiException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CustomerDeterminationService {

    final CustomerService customerService;

    public Mono<Customer> determineCustomerForTransaction(final CustomerLookupDetail customerLookupDetail){
        Mono<Customer> customerMono = Mono.empty();
        final String externalReference = customerLookupDetail.customerExternalReference();
        final String source = customerLookupDetail.customerSource();
        final UUID customerId = customerLookupDetail.customerId();
        if (customerId != null){
            customerMono = customerService.findByComplytId(customerId);
        } else if (StringChecker.isInputValid(customerLookupDetail.customerExternalReference(), customerLookupDetail.customerSource())) {
            customerMono = customerService.findByExternalIdAndSource(externalReference, source);
        }
        return customerMono.switchIfEmpty(Mono.error(CustomerNotFoundApiException::new));
    }
}
