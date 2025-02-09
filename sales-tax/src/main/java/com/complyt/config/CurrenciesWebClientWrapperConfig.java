package com.complyt.config;

import com.complyt.business.strategy.currencyExchange.ComplytCurrenciesWebClientWrapper;
import com.complyt.business.strategy.currencyExchange.CurrenciesWebClientWrapper;
import com.complyt.business.strategy.currencyExchange.StubCurrenciesWebClientWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CurrenciesWebClientWrapperConfig {

    @Profile({"complytCurrencyEngine"})
    @Bean("currenciesWebClientWrapper")
    public CurrenciesWebClientWrapper currenciesWebClientWrapper(@Autowired WebClient complytCurrencyEngineWebClient) {
        return new ComplytCurrenciesWebClientWrapper(complytCurrencyEngineWebClient);
    }

    @Profile({"complytStubCurrency", "default"})
    @Bean("currenciesWebClientWrapper")
    public CurrenciesWebClientWrapper stubCurrenciesWebClientWrapper() {
        return new StubCurrenciesWebClientWrapper();
    }

}