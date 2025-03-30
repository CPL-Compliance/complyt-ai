package io.complyt.authentication.services;

import io.complyt.authentication.domain.Partnership;
import io.complyt.authentication.domain.Referral;
import io.complyt.authentication.domain.enums.PartnershipStatus;
import io.complyt.authentication.domain.timestamps.Timestamps;
import io.complyt.authentication.repositories.PartnershipRepository;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.ReferralsNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.SpecificReferralNotFoundApiException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
@EqualsAndHashCode
@Service
public class PartnershipService {
    @NonNull
    PartnershipRepository partnershipRepository;

    public Mono<Partnership> findPartnership() {
        return partnershipRepository.findPartnership();
    }

    public Mono<Partnership> findByTenantId(final @NonNull String tenantId) {
        return partnershipRepository.findPartnershipByPartnerTenantId(tenantId)
                .switchIfEmpty(Mono.error(new ReferralsNotFoundApiException()));
    }

    public Mono<List<String>> findSupportedTenantsForPartnerByTenantId(final @NonNull String tenantId) {
        return findByTenantId(tenantId)
                .map(Partnership::getSupportedReferrals)
                .map(referrals -> referrals.stream()
                        .filter(referral -> referral.getPartnershipStatus() == PartnershipStatus.ACTIVE)
                        .map(Referral::getTenantId)
                        .collect(Collectors.toList()))
                .flatMap(list -> list.isEmpty() ? Mono.error(new ReferralsNotFoundApiException()) : Mono.just(list));
    }

    public Mono<Partnership> upsertReferralClient(Referral referral) {
        return partnershipRepository.findPartnership()
                .flatMap(partnershipDocument -> {
                    List<Referral> existingReferralList = partnershipDocument.getSupportedReferrals().stream()
                            .filter(existingReferral -> existingReferral.getTenantId().equals(referral.getTenantId()) && existingReferral.getPartnershipStatus().equals(PartnershipStatus.ACTIVE)).toList();

                    if (!existingReferralList.isEmpty()) {
                        referral.setPartnershipStatus(PartnershipStatus.ACTIVE).setTimestamps(new Timestamps(existingReferralList.get(0).getTimestamps().getCreatedDate(), LocalDateTime.now()));
                        return partnershipRepository.updateReferral(referral);
                    } else {
                        referral.setPartnershipStatus(PartnershipStatus.ACTIVE).setTimestamps(new Timestamps(LocalDateTime.now(), LocalDateTime.now()));
                        return partnershipRepository.saveReferral(referral);
                    }
                })
                .switchIfEmpty(Mono.error(new PartnerNotFoundApiException()));
    }

    public Mono<Partnership> markAsCancelledReferralClient(String tenantId) {
        return partnershipRepository.findPartnership()
                .flatMap(partnershipDocument -> {
                    List<Referral> existingReferralList = partnershipDocument.getSupportedReferrals().stream()
                            .filter(existingReferral -> existingReferral.getTenantId().equals(tenantId) && existingReferral.getPartnershipStatus().equals(PartnershipStatus.ACTIVE)).toList();

                    Referral updatedReferralToDelete = !existingReferralList.isEmpty() ?
                            existingReferralList.get(0).setPartnershipStatus(PartnershipStatus.CANCELLED).setTimestamps(new Timestamps(existingReferralList.get(0).getTimestamps().getCreatedDate(), LocalDateTime.now())) :
                            null;

                    return updatedReferralToDelete != null ?
                            partnershipRepository.updateReferral(updatedReferralToDelete) :
                            Mono.error(new SpecificReferralNotFoundApiException());
                })
                .switchIfEmpty(Mono.error(new PartnerNotFoundApiException()));
    }
}
