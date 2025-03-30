package io.complyt.authentication.facades;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.services.PartnershipService;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.SpecificReferralNotFoundApiException;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Component
public class PartnershipFacade {

    @NonNull
    PartnershipService partnershipService;

    public Mono<Partnership> getPartnership() {
        return partnershipService.findPartnership()
                .switchIfEmpty(Mono.error(new PartnerNotFoundApiException()));
    }

    public Mono<Partnership> upsertReferralClient(Referral referral) {
        return partnershipService.upsertReferralClient(referral)
                .switchIfEmpty(Mono.error(new PartnerNotFoundApiException()));
    }

    public Mono<Partnership> markReferralAsCancelled(String tenantId) {
        return partnershipService.markAsCancelledReferralClient(tenantId)
                .switchIfEmpty(Mono.error(new SpecificReferralNotFoundApiException()));
    }
}