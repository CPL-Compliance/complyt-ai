package com.complyt.business.complyt_id;

import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoArgsConstructor
@Component
@Slf4j
public class SalesTaxTrackingComplytIdHandler implements ComplytIdHandler<SalesTaxTracking> {
    @Override
    public Mono<SalesTaxTracking> isComplytIdOfUpdatedEqualsToOld(SalesTaxTracking newSalesTaxTracking, SalesTaxTracking oldSalesTaxTracking) {
        return newSalesTaxTracking.getComplytId() == null || newSalesTaxTracking.getComplytId().equals(oldSalesTaxTracking.getComplytId()) ?
                Mono.just(newSalesTaxTracking) : Mono.empty();
    }

    @Override
    public Mono<SalesTaxTracking> isNewDontHaveComplytId(SalesTaxTracking newSalesTaxTracking) {
        return newSalesTaxTracking.getComplytId() == null ?
                Mono.just(newSalesTaxTracking) : Mono.empty();
    }

    @Override
    public SalesTaxTracking insertComplytIdToNew(SalesTaxTracking newSalesTaxTracking) {
        return newSalesTaxTracking.withComplytId(UUID.randomUUID());
    }
}
