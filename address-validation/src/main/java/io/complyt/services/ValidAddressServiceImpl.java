package io.complyt.services;

import io.complyt.business.address.CountryIsUsaChecker;
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
import java.util.Objects;

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
                        .flatMap(addressData -> saveAddressIfNeeded(addressData, alignedAddress))));
    }

    // If there's a mismatch in the validate endpoint, the corrected (validated) address is returned to the client.
    // In contrast, the resolve endpoint returns an error in case of a mismatch.
    public Mono<ValidatedAddress> saveAddressIfNeeded(List<CachedAddressData> addressData, Address address) {

        return setBeforeSave(addressData, address)
                .flatMap(validatedAddress -> resolveBestMatchAddress(addressData)
                        .flatMap(bestMatchedAddress -> hereAddressChecker.isCountryAndStateMatch(bestMatchedAddress, address)
                                ? saveAddress(validatedAddress)
                                : Mono.just(validatedAddress)));

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
        Address alignedAddress = addressAligner.alignForOutsource(address);
        return findByAddressClientWrapper(alignedAddress)
                .flatMap(addressData -> hereAddressChecker.filterValidAddresses(addressData)
                        .switchIfEmpty(Mono.defer(() -> resolveFallbackAddress(addressData, alignedAddress))
                                .flatMap(hereAddressChecker::filterValidAddresses))
                        .switchIfEmpty(Mono.defer(() -> resolveSecondFallbackAddress(addressData, alignedAddress)
                                .flatMap(hereAddressChecker::filterValidAddresses))));
    }

    Mono<List<CachedAddressData>> findByAddressClientWrapper(Address address) {
        Address alignedAddress = addressAligner.alignForOutsource(address);
        return findByAddress(address)
                .flatMap(validated -> Mono.just(validated.getMatchedAddresses()))
                .flatMap(hereAddressChecker::filterValidAddresses)
                .switchIfEmpty(Mono.defer(() -> hereAddressValidationClientWrapper.validateAddress(alignedAddress)
                        .map(HereAddressToAddressMapper.INSTANCE::map)));
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

    private Mono<List<CachedAddressData>> resolveCountryWithoutZip(List<CachedAddressData> addressData, Address address) {
        return resolveBestMatchAddress(addressData)
                .flatMap(bestMatchedAddress ->
                        (Objects.equals(bestMatchedAddress.address().zip(), address.zip()) &&
                                !Objects.equals(bestMatchedAddress.address().country(), address.country()))
                                ? findByAddressClientWrapper(address.withZip(null))
                                : Mono.just(addressData)
                )
                .switchIfEmpty(Mono.defer(() -> findByAddressClientWrapper(address.withZip(null))));
    }

    private Mono<List<CachedAddressData>> resolveOnlyCountry(List<CachedAddressData> addressData, Address address) {
        return resolveBestMatchAddress(addressData)
                .flatMap(bestMatchedAddress ->
                        !Objects.equals(bestMatchedAddress.address().country(), address.country())
                                ? findByAddressClientWrapper(Address.builder().country(address.country()).build())
                                : Mono.just(addressData)
                )
                .switchIfEmpty(Mono.defer(() -> findByAddressClientWrapper(Address.builder().country(address.country()).build())));
    }

    Mono<List<CachedAddressData>> resolveFallbackAddress(List<CachedAddressData> addressData, Address address) {
        return CountryIsUsaChecker.isCountryUsa(address.country()) ?
                resolveStreetIfMissing(addressData, address) :
                resolveCountryWithoutZip(addressData, address);
    }

    Mono<List<CachedAddressData>> resolveSecondFallbackAddress(List<CachedAddressData> addressData, Address address) {
        return CountryIsUsaChecker.isCountryUsa(address.country()) ?
                Mono.just(addressData) :
                resolveOnlyCountry(addressData, address);
    }

    private Mono<CachedAddressData> resolveBestMatchAddress(List<CachedAddressData> addresses) {
        return Mono.justOrEmpty(addresses)
                .filter(list -> !list.isEmpty())
                .map(list -> list.get(0));
    }
}