package io.complyt.services;

import io.complyt.business.address_aligner.AddressAligner;
import io.complyt.business.address_checkers.HereAddressChecker;
import io.complyt.business.webclients.addressvalidations.AddressValidationWebClientWrapper;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.mappers.HereAddressToAddressMapper;
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
import java.util.List;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Slf4j
public class ValidAddressServiceImpl implements ValidAddressService {

    @NonNull
    private ValidationAddressRepositoryImpl validationAddressRepositoryImpl;

    @NonNull
    private AddressValidationWebClientWrapper hereAddressValidationClientWrapper;

    @NonNull
    private HereAddressChecker hereAddressChecker;

    @NonNull
    private AddressAligner addressAligner;

    @Override
    public Mono<ValidatedAddress> validateAddress(Address address) {
        Address alignedAddress = addressAligner.alignGlobalAddress(address);
        return findByAddress(alignedAddress)
                .switchIfEmpty(Mono.defer(() -> fetchAndValidateAddress(alignedAddress)
                        .flatMap(addressData -> setBeforeSave(addressData, alignedAddress))
                        .flatMap(this::saveAddress)));
    }

    @Override
    public Mono<CachedAddressData> resolveAddress(Address address) {
        return validateAddress(address)
                .flatMap(matchedAddresses -> resolveBestMatchAddress(matchedAddresses.getMatchedAddresses())
                        .flatMap(bestMatchAddress -> hereAddressChecker.validateCountryAndStateMatch(bestMatchAddress, address)));
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

    private Mono<List<CachedAddressData>> fetchAndValidateAddress(Address address) {
        return findByAddressClientWrapper(address)
                .flatMap(addressData -> hereAddressChecker.filterValidAddresses(addressData)
                        .switchIfEmpty(Mono.defer(() -> resolveStreetIfMissing(addressData, address))
                                .flatMap(hereAddressChecker::filterValidAddresses)));
    }

    private Mono<List<CachedAddressData>> findByAddressClientWrapper(Address address) {
        Address alignedAddress = addressAligner.alignForOutsource(address);
        return hereAddressValidationClientWrapper.validateAddress(alignedAddress)
                .map(HereAddressToAddressMapper.INSTANCE::map);
    }

    private Mono<ValidatedAddress> setBeforeSave(List<CachedAddressData> addressData, Address address) {
        return Mono.just(new ValidatedAddress(null, addressData, address, LocalDateTime.now()));
    }

    private Mono<List<CachedAddressData>> resolveStreetIfMissing(List<CachedAddressData> addressData, Address address) {
        return resolveBestMatchAddress(addressData)
                .flatMap(bestMatchedAddress -> (bestMatchedAddress.address().street() == null && address.street() != null)
                        ? findByAddressClientWrapper(address.withStreet(null))
                        : Mono.just(addressData)); // By default, returns the data back
    }

    private Mono<CachedAddressData> resolveBestMatchAddress(List<CachedAddressData> addresses) {
        return Mono.justOrEmpty(addresses)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0));
    }
}
