package com.complyt.business.timestamps_injection;

import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class SalesTaxTrackingRegisteredDateTimestampsInjector implements TimestampsInjector<SalesTaxTracking> {

    @NonNull
    private final SalesTaxTracking salesTaxTracking;

    @Override
    public SalesTaxTracking inject() {
        LocalDateTime timestamp = LocalDateTime.now();
        return salesTaxTracking.withRegistrationDate(timestamp);
    }
}