package io.complyt.services;

import io.complyt.business.address_checkers.HereAddressChecker;
import io.complyt.business.webclients.addressvalidations.AddressValidationWebClientWrapper;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.mappers.HereAddressToAddressMapper;
import io.complyt.domain.mappers.ValidatedAddressToAddressMapper;
import io.complyt.repositories.ValidationAddressRepositoryImpl;
import io.complyt.utils.observability.ContextLogger;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ValidAddressServiceImpl implements ValidAddressService {

    @NonNull
    private ValidationAddressRepositoryImpl validationAddressRepositoryImpl;

    @NonNull
    private AddressValidationWebClientWrapper addressValidationWebClientWrapper;

    @NonNull
    private HereAddressChecker hereAddressChecker;

    @Override
    public Mono<Address> validateAddress(Address address) {
        return findByAddress(address)
                .map(ValidatedAddressToAddressMapper.INSTANCE::map)
                .switchIfEmpty(Mono.defer(() -> findByAddressClientWrapper(address)
                        .flatMap(addressData -> setBeforeSave(addressData, address))
                        .flatMap(this::saveAddress)
                        .map(ValidatedAddressToAddressMapper.INSTANCE::map)));
    }

    public Mono<ValidatedAddress> findByAddress(@NonNull Address address) {
        return validationAddressRepositoryImpl.findAddress(address)
                .doOnNext(foundAddress -> log.info("<-- Address found in db: {}", foundAddress))
                .switchIfEmpty(Mono.defer(() -> ContextLogger.observeCtx("<-- did not found address in db, returning empty", log::info)
                                .then(Mono.empty())));
    }

    public Mono<ValidatedAddress> saveAddress(@NonNull ValidatedAddress address) {
        return validationAddressRepositoryImpl.saveAddress(address);
    }

    private Mono<CachedAddressData> findByAddressClientWrapper(Address address) {
        return addressValidationWebClientWrapper.validateAddress(address)
                .map(HereAddressToAddressMapper.INSTANCE::map)
                .flatMap(addressData -> hereAddressChecker.checkAddress(addressData, address));
    }

    private Mono<ValidatedAddress> setBeforeSave(CachedAddressData addressData, Address address) {
        addressData = addressData.withPartial(address.isPartial());

        return Mono.just(new ValidatedAddress(null, addressData, address, LocalDateTime.now()));
    }
}
