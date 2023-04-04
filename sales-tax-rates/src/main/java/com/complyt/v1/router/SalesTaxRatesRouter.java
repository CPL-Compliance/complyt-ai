package com.complyt.v1.router;

import com.complyt.v1.api_info.sales_tax_rates.GetSalesTaxRatesByAddressApiInfo;
import com.complyt.v1.handler.SalesTaxRatesHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class SalesTaxRatesRouter {

    public static final String BASE_URL = "/v1/sales_tax_rates";

    @Bean
    @GetSalesTaxRatesByAddressApiInfo
    public RouterFunction<ServerResponse> getSalesTaxRatesByAddress(@NonNull final SalesTaxRatesHandler salesTaxRatesHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, salesTaxRatesHandler::getSalesTaxRatesByAddress);
    }

}
