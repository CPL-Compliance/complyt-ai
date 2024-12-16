package io.complyt.services;

import io.complyt.business.address.UsaAbbreviations;
import io.complyt.business.address_checkers.HereAddressChecker;
import io.complyt.business.webclients.addressvalidations.AddressValidationWebClientWrapper;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.domain.mappers.ValidatedAddressToAddressMapper;
import io.complyt.repositories.ValidationAddressRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidAddressServiceImplTest {

    @InjectMocks
    private ValidAddressServiceImpl validAddressService;

    @Mock
    private ValidationAddressRepositoryImpl validationAddressRepositoryImpl;

    @Mock
    private AddressValidationWebClientWrapper addressValidationWebClientWrapper;

    @Mock
    private HereAddressChecker hereAddressChecker;

    private Address address;
    private ValidatedAddress validatedAddress;
    private CachedAddressData cachedAddressData;
    private HereAddressData hereAddressData;

    @BeforeEach
    void setUp() {
        address = TestUtilities.getAddress();
        cachedAddressData = TestUtilities.getCachedAddressData();
        validatedAddress = TestUtilities.getValidatedAddress();
        hereAddressData = TestUtilities.getHereAddressData();
    }

    @Test
    void validateAddress_AddressFoundInRepository_ReturnsMappedAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressNotFoundInRepository_FindsAndSavesNewAddress() {
        Address alignedAddress = address.withCountry(UsaAbbreviations.DEFAULT_COUNTRY);
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());
        when(addressValidationWebClientWrapper.validateAddress(alignedAddress)).thenReturn(Mono.just(hereAddressData));
        when(hereAddressChecker.checkAddress(cachedAddressData, alignedAddress)).thenReturn(Mono.just(cachedAddressData));
        when(validationAddressRepositoryImpl.saveAddress(any())).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void validateAddress_AddressFoundInRepository_FindsAndSavesNewAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));;

        StepVerifier.create(validAddressService.validateAddress(address))
                .expectNext(ValidatedAddressToAddressMapper.INSTANCE.map(validatedAddress))
                .verifyComplete();
    }

    @Test
    void findByAddress_AddressFound_ReturnsValidatedAddress() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.findByAddress(address))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void findByAddress_AddressNotFound_ReturnsEmpty() {
        when(validationAddressRepositoryImpl.findAddress(address)).thenReturn(Mono.empty());

        StepVerifier.create(validAddressService.findByAddress(address))
                .verifyComplete();
    }

    @Test
    void saveAddress_ValidAddress_ReturnsSavedValidatedAddress() {
        when(validationAddressRepositoryImpl.saveAddress(validatedAddress)).thenReturn(Mono.just(validatedAddress));

        StepVerifier.create(validAddressService.saveAddress(validatedAddress))
                .expectNext(validatedAddress)
                .verifyComplete();
    }

    @Test
    void findByAddress_NullAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validAddressService.findByAddress(null));
    }

    @Test
    void saveAddress_NullValidatedAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validAddressService.saveAddress(null));
    }

    @Test
    void validateAddress_NullValidatedAddress_ThrowsNullPointerException() {
        assertThrows(NullPointerException.class, () -> validAddressService.validateAddress(null));
    }
}