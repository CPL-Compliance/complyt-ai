package com.complyt.business.exemption;

import com.complyt.domain.customer.exemption.Exemption;
import lombok.NonNull;
import reactor.core.publisher.Flux;

public interface ListGenerator<T> {
    Flux<Exemption> generate(@NonNull T t);

}
