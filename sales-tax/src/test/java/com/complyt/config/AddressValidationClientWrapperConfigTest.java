package com.complyt.config;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.business.address_validation.ComplytAddressValidationWebClientWrapper;
import com.complyt.business.address_validation.StubAddressValidationWebClientWrapper;
import com.complyt.proxies.AddressValidationServiceProxy;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AddressValidationClientWrapperConfigTest {
    @InjectMocks
    AddressValidationClientWrapperConfig addressValidationClientWrapperConfig;
    @Mock
    AddressValidationServiceProxy addressValidationServiceProxy;

    @Test
    void testComplytAddressValidationWebClientWrapper() {
        AddressValidationWebClientWrapper<MatchedAddressDataDto> expectedAddressAddressValidationWebClientWrapper = new ComplytAddressValidationWebClientWrapper(addressValidationServiceProxy);
        AddressValidationWebClientWrapper<MatchedAddressDataDto> addressAddressValidationWebClientWrapper = addressValidationClientWrapperConfig.complytAddressValidationWebClientWrapper(addressValidationServiceProxy);
        assertEquals(expectedAddressAddressValidationWebClientWrapper, addressAddressValidationWebClientWrapper);
    }

    @Test
    void testStubComplytAddressValidationWebClientWrapper() {
        StubAddressValidationWebClientWrapper expectedStubValidationAddress = new StubAddressValidationWebClientWrapper();
        AddressValidationWebClientWrapper<MatchedAddressDataDto> addressAddressValidationWebClientWrapper = addressValidationClientWrapperConfig.stubComplytAddressValidationWebClientWrapper(addressValidationServiceProxy);
        assertEquals(expectedStubValidationAddress, addressAddressValidationWebClientWrapper);

    }



}