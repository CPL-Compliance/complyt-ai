package io.complyt.services;

import io.complyt.business.address.CountryToStandardizedCountry;
import io.complyt.business.address_checkers.HereAddressChecker;
import io.complyt.business.external_fetcher.FastTaxGetBestMatchCityCountyFetcher;
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
    private AddressValidationWebClientWrapper hereAddressValidationClientWrapper;

    @NonNull
    private AddressValidationWebClientWrapper fastTaxWebClientWrapper;

    @NonNull
    private FastTaxGetBestMatchCityCountyFetcher fastTaxGetBestMatchCityCountyFetcher;

    @NonNull
    private HereAddressChecker hereAddressChecker;

    @Override
    public Mono<Address> validateAddress(Address address) {
        return findByAddress(address)
                .map(ValidatedAddressToAddressMapper.INSTANCE::map)
                .switchIfEmpty(Mono.defer(() -> fetchAndValidateAddress(address)
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

    private Mono<CachedAddressData> fetchAndValidateAddress(Address address) {
        return findByAddressClientWrapper(address)
                .flatMap(addressData -> hereAddressChecker.checkAddress(addressData, address)
                        .switchIfEmpty(Mono.defer(() -> resolveStreetIfMissing(addressData, address))
                                .flatMap(addressDataNoStreet -> hereAddressChecker.checkAddress(addressDataNoStreet, address))))
                .flatMap(addressData -> resolveCountyIfMissing(addressData, address));
    }

    private Mono<CachedAddressData> findByAddressClientWrapper(Address address) {
        Address alignedAddress = address.withCountry(CountryToStandardizedCountry.standardize(address.country()));
        return hereAddressValidationClientWrapper.validateAddress(alignedAddress)
                .map(HereAddressToAddressMapper.INSTANCE::map);
    }

    private Mono<ValidatedAddress> setBeforeSave(CachedAddressData addressData, Address address) {
        addressData = addressData.withPartial(address.isPartial());

        return Mono.just(new ValidatedAddress(null, addressData, address, LocalDateTime.now()));
    }

    private Mono<CachedAddressData> resolveStreetIfMissing(CachedAddressData cachedData, Address address) {
        return cachedData.street() == null ? findByAddressClientWrapper(address.withStreet(null))
                : Mono.just(cachedData);
    }

    private Mono<CachedAddressData> resolveCountyIfMissing(CachedAddressData cachedData, Address address) {
        return cachedData.county() == null ? fastTaxWebClientWrapper.validateAddress(address)
                .flatMap(validatedAddress -> fastTaxGetBestMatchCityCountyFetcher.fetch(validatedAddress, cachedData))
                : Mono.just(cachedData);
    }
}
