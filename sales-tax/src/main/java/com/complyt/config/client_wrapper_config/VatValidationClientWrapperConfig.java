package com.complyt.config.client_wrapper_config;

//import com.complyt.business.vat_validation.web_clients.StubVatValidationWebClientWrapper;
import com.complyt.business.vat_validation.web_clients.VatValidationWebClientWrapper;
import com.complyt.business.vat_validation.web_clients.VowVatValidationWebClientWrapper;
import com.complyt.config.WebClientWrapperProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class VatValidationClientWrapperConfig {
    @Profile({"vowVatValidation"})
    @Bean("vatValidationWebClientWrapper")
    public VatValidationWebClientWrapper vowVatValidationWebClientWrapper(@Autowired WebClient vowVatValidationWebClient,
                                                                          @Autowired WebClientWrapperProperties vowVatValidationWebClientWrapperProperties) {
        return new VowVatValidationWebClientWrapper(
                vowVatValidationWebClient,
                vowVatValidationWebClientWrapperProperties.getScheme(),
                vowVatValidationWebClientWrapperProperties.getHost(),
                vowVatValidationWebClientWrapperProperties.getPath()
        );
    }

//    @Profile({"stubVatValidation", "default"})
//    @Bean("vatValidationWebClientWrapper")
//    public StubVatValidationWebClientWrapper stubVatValidationWebClientWrapper(WebClientWrapperProperties vowVatValidationWebClientWrapperProperties) {
//        return new StubVatValidationWebClientWrapper(null, "", "", "");
//    }
}