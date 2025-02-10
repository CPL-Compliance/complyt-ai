package com.complyt.config;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.business.address_validation.ComplytAddressValidationWebClientWrapper;
import com.complyt.business.address_validation.StubAddressValidationWebClientWrapper;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.complyt.proxies.AddressValidationServiceProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AddressValidationClientWrapperConfig {

    @Profile({"internalSalesTax", "internalRatesSystemTestProfile", "fastTax", "default"})
    @Bean("addressValidationWebClientWrapper")
    public AddressValidationWebClientWrapper<MatchedAddressData> complytAddressValidationWebClientWrapper(AddressValidationServiceProxy addressValidationServiceProxy){
        return new ComplytAddressValidationWebClientWrapper(addressValidationServiceProxy);
    }

    @Profile({"stubInternalRates", "stubFastTax"})
    @Bean("addressValidationWebClientWrapper")
    public StubAddressValidationWebClientWrapper stubComplytAddressValidationWebClientWrapper(AddressValidationServiceProxy addressValidationServiceProxy){
        return new StubAddressValidationWebClientWrapper();
    }
}
