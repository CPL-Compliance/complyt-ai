package com.complyt.business.complyt_id;

import com.complyt.domain.customer.Customer;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoArgsConstructor
@Component
@Slf4j
public class CustomerComplytIdHandler implements ComplytIdHandler<Customer> {
    @Override
    public Mono<Customer> checkComplytIdOfUpdatedEqualsToOld(Customer newCustomer, Customer oldCustomer) {
        return newCustomer.getComplytId() == null || newCustomer.getComplytId().equals(oldCustomer.getComplytId()) ?
                Mono.just(newCustomer) : Mono.error(new NotFoundException("complyt ids of modified and original customers are not equal"));
    }

    @Override
    public Mono<Customer> checkNewDontHaveComplytId(Customer newCustomer) {
        return newCustomer.getComplytId() == null ?
                Mono.just(newCustomer) : Mono.error(new NotFoundException("cannot insert new customer with complyt id"));
    }

    @Override
    public Customer insertComplytIdToNew(Customer newCustomer) {
        return newCustomer.withComplytId(UUID.randomUUID());
    }
}
