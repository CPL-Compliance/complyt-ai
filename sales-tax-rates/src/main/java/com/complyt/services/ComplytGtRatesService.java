package com.complyt.services;

import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.repositories.gt_rates.ComplytGtRatesRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ComplytGtRatesService {

    @NonNull
    ComplytGtRatesRepository complytGtRatesRepository;

    public Mono<ComplytGtRates> findByAddress(@NonNull GtAddress gtAddress) {
        return complytGtRatesRepository.findByAddress(gtAddress);
    }
}
