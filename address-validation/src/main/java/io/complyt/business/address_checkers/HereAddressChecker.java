package io.complyt.business.address_checkers;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.utils.exceptions.types.ZipCodeMismatchException;
import io.complyt.utils.observability.ContextLogger;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static io.complyt.business.OutSourceThresholdToPass.THRESHOLD_SCORE_TO_PASS;

@Slf4j
@Component
public class HereAddressChecker {

    public Mono<CachedAddressData> checkAddress(@NonNull CachedAddressData data, @NonNull Address requestAddress) {
       return checkScoreAddress(data)
               .flatMap(addressPassedScore -> approveResponseIfZipIncludesRequestZip(addressPassedScore, requestAddress));
    }

    private Mono<CachedAddressData> checkScoreAddress(CachedAddressData data) {
        return data.score() >= THRESHOLD_SCORE_TO_PASS ?
                ContextLogger.observeCtx("--> here address score surpassed thresholdScore of " + THRESHOLD_SCORE_TO_PASS + ": " + data, log::info)
                        .then(Mono.just(data))
                : ContextLogger.observeCtx("--> here address score did not surpass thresholdScore of " + THRESHOLD_SCORE_TO_PASS + ": " + data, log::info)
                .then(Mono.empty());
    }

    private Mono<CachedAddressData> approveResponseIfZipIncludesRequestZip(CachedAddressData outSourceResponseAddress, Address requestAddress) {
        String requestZip = requestAddress.zip();
        return (outSourceResponseAddress.zip() == null || outSourceResponseAddress.zip().isEmpty()) ?
                ContextLogger.observeCtx("here address does not include zip, address was not found: " + outSourceResponseAddress, log::info)
                        .then(Mono.empty())
                : Mono.just(outSourceResponseAddress.zip())
                .flatMap(outSourceZip -> (outSourceZip.startsWith(requestZip) || (requestZip.startsWith(outSourceZip)))  ?
                        ContextLogger.observeCtx("here address includes the correct zip, returning address: " + outSourceResponseAddress, log::info)
                                .then(Mono.just(outSourceResponseAddress))
                        : ContextLogger.observeCtx("here address zip does not match request zip. request zip: " + requestZip + ", data: " + outSourceResponseAddress, log::info)
                        .then(Mono.error(new ZipCodeMismatchException(requestZip, outSourceResponseAddress.zip(), requestAddress.toString()))));
    }
}