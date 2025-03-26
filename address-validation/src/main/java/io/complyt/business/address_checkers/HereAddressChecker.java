package io.complyt.business.address_checkers;

import io.complyt.business.OutSourceThresholdToPass;
import io.complyt.business.address.CountryIsUsaChecker;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.utils.exceptions.types.ObjectNotValidException;
import io.complyt.v1.config.error_messages.GenericErrorMessages;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class HereAddressChecker {

    public Mono<CachedAddressData> validateCountryAndStateMatch(@NonNull CachedAddressData data, @NonNull Address requestAddress) {
        boolean isCountryMismatch = data.scoring().fieldScore().countryMatch().equals(FieldMatchType.NO_MATCH);
        boolean isUSA = CountryIsUsaChecker.isCountryUsa(requestAddress.country());

        // Check Country match
        if (isCountryMismatch) {
            return Mono.error(new ObjectNotValidException(String.format(GenericErrorMessages.COUNTRY_CODE_MISMATCH, requestAddress.country(), data.address().country())));
        }

        // Check State match in USA
        FieldMatchType isStateMismatch = data.scoring().fieldScore().stateMatch();
        if (isUSA && isStateMismatch != null && isStateMismatch.equals(FieldMatchType.NO_MATCH)) {
            return Mono.error(new ObjectNotValidException(String.format(GenericErrorMessages.STATE_CODE_MISMATCH, requestAddress.state(), data.address().state())));
        }
        // Valid
        return Mono.just(data);
    }

    public Mono<List<CachedAddressData>> filterValidAddresses(List<CachedAddressData> cachedAddresses) {
        return Flux.fromIterable(cachedAddresses)
                .filter(this::isValidAddress)
                .doOnNext(data -> log.info("--> here address is valid:{}", data))
                .doOnDiscard(CachedAddressData.class, data -> log.info("--> here address is not valid: {}", data))
                .collectList()
                .filter(list -> !list.isEmpty()); // Passes only non-empty lists for fallback
    }

    boolean isValidAddress(CachedAddressData item) {
        if (item == null || item.scoring() == null || item.address() == null) {
            log.warn("CachedAddressData is invalid due to null fields: {}", item);
            return false;
        }

        boolean hasValidScore = item.scoring().score() >= OutSourceThresholdToPass.THRESHOLD_SCORE_TO_PASS;
        boolean isUSA = CountryIsUsaChecker.isCountryUsa(item.address().country());
        boolean hasRequiredFields = !isUSA || item.address().zip() != null && item.address().county() != null;

        if (!hasValidScore || !hasRequiredFields) {
            log.debug("Address validation failed: score valid = {}, fields valid = {} for {}", hasValidScore, hasRequiredFields, item);
        }

        return hasValidScore && hasRequiredFields;
    }
}