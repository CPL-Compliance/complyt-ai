package com.complyt.business.address_validation;

import com.complyt.business.exceptions.ComplytAddressValidationException;
import com.complyt.domain.Address;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.complyt.proxies.AddressValidationServiceProxy;
import com.complyt.v1.exceptions.FeignErrorUtils;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;


@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComplytAddressValidationWebClientWrapper implements AddressValidationWebClientWrapper<MatchedAddressData> {

    AddressValidationServiceProxy addressValidationServiceProxy;

    @Override
    public Mono<MatchedAddressData> validateAddress(Address address) {
        return validateAddress(address.city(), address.country(), address.county(), address.state(), address.street(), address.zip(), address.isPartial());
    }

    @Override
    public Mono<MatchedAddressData> validateAddress(String city, String country, String county, String state, String street, String zip, boolean isPartial) {
        return addressValidationServiceProxy.validateAddress(state, country, county, city, street, zip, isPartial)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(10))
                        .filter(throwable -> !(throwable instanceof FeignException.BadRequest))
                        .onRetryExhaustedThrow(
                                ((retryBackoffSpec, retrySignal) ->
                                        new ComplytAddressValidationException(retrySignal.totalRetries() + " Retries Exhausted")
                                ))).onErrorMap(FeignException.BadRequest.class, notValid ->
                        new ObjectNotValidApiException(FeignErrorUtils.extractErrorMessage(notValid)));
    }
}
