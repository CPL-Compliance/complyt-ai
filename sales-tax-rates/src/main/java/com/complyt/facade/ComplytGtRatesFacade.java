package com.complyt.facade;

import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.services.ComplytGtRatesService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class ComplytGtRatesFacade {

    @NonNull
    ComplytGtRatesService complytGtRatesService;

    public Mono<ComplytGtRates> findByAddress(@NonNull GtAddress gtAddress) {
        return complytGtRatesService.findByAddress(gtAddress);
    }
}
