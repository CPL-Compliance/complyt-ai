package com.complyt.config.client_wrapper_config;


import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.business.address_validation.ComplytAddressValidationWebClientWrapper;
import com.complyt.business.address_validation.StubAddressValidationWebClientWrapper;
import com.complyt.proxies.AddressValidationServiceProxy;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AddressValidationClientWrapperConfig {

    @Profile({"complytTaxEngine"})
    @Bean("addressValidationWebClientWrapper")
    public AddressValidationWebClientWrapper<MatchedAddressDataDto> complytAddressValidationWebClientWrapper(AddressValidationServiceProxy addressValidationServiceProxy){
        return new ComplytAddressValidationWebClientWrapper(addressValidationServiceProxy);
    }

    @Profile({"complytStubTax", "default"})
    @Bean("addressValidationWebClientWrapper")
    public StubAddressValidationWebClientWrapper stubComplytAddressValidationWebClientWrapper(AddressValidationServiceProxy addressValidationServiceProxy){
        return new StubAddressValidationWebClientWrapper();
    }
}
