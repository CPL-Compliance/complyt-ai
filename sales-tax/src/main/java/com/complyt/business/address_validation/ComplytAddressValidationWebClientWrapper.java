package com.complyt.business.address_validation;

import com.complyt.business.exceptions.ComplytAddressValidationException;
import com.complyt.business.exceptions.FeignErrorUtils;
import com.complyt.domain.transaction.Address;
import com.complyt.proxies.AddressValidationServiceProxy;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import com.complyt.v1.mappers.AddressMapper;
import feign.FeignException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;


@EqualsAndHashCode
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComplytAddressValidationWebClientWrapper implements AddressValidationWebClientWrapper<Address> {

    @NonNull
    AddressValidationServiceProxy addressValidationServiceProxy;

    @Override
    public Mono<Address> validateAddress(Address address) {
        return validateAddress(address.city(), address.country(), address.county(), address.state(), address.street(), address.zip(), address.isPartial());
    }

    @Override
    public Mono<Address> validateAddress(String city, String country, String county, String state, String street, String zip, boolean isPartial) {
        return addressValidationServiceProxy.validateAddress(state, country, county, city, street, zip, isPartial)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(10))
                        .filter(throwable -> !(throwable instanceof FeignException.BadRequest))
                        .onRetryExhaustedThrow(
                                ((retryBackoffSpec, retrySignal) ->
                                        new ComplytAddressValidationException(retrySignal.totalRetries() + " Retries Exhausted")
                                )))
                .map(AddressMapper.INSTANCE::mandatoryAddressDtoToAddress)
                .onErrorMap(FeignException.BadRequest.class, notValid ->
                        new ObjectNotValidApiException(FeignErrorUtils.extractErrorMessage(notValid)));
    }
}
