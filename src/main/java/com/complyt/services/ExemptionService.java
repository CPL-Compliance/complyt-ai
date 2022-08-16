package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface ExemptionService extends CrudService<Exemption,String> {
    Mono<Exemption> findByClientCustomerAndState(@NonNull Transaction transaction);
}
