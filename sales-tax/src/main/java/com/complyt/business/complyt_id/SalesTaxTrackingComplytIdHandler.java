package com.complyt.business.complyt_id;

import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@NoArgsConstructor
@Component
@Slf4j
public class SalesTaxTrackingComplytIdHandler implements ComplytIdHandler<SalesTaxTracking> {
    @Override
    public Mono<SalesTaxTracking> checkComplytIdOfUpdatedEqualsToOld(SalesTaxTracking newSalesTaxTracking, SalesTaxTracking oldSalesTaxTracking) {
        return newSalesTaxTracking.getComplytId() == null || newSalesTaxTracking.getComplytId().equals(oldSalesTaxTracking.getComplytId()) ?
                Mono.just(newSalesTaxTracking) : Mono.error(new NotFoundException("complyt ids of modified and original salesTaxTrackings are not equal"));
    }

    @Override
    public Mono<SalesTaxTracking> checkNewDontHaveComplytId(SalesTaxTracking newSalesTaxTracking) {
        return newSalesTaxTracking.getComplytId() == null ?
                Mono.just(newSalesTaxTracking) : Mono.error(new NotFoundException("cannot insert new salesTaxTracking with complyt id"));
    }

    @Override
    public SalesTaxTracking insertComplytIdToNew(SalesTaxTracking newSalesTaxTracking) {
        return newSalesTaxTracking.withComplytId(UUID.randomUUID());
    }
}
