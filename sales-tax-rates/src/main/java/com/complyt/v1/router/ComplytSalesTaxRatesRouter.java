package com.complyt.v1.router;

import com.complyt.v1.api_info.sales_tax_rates.GetSalesTaxRatesByAddressApiInfo;
import com.complyt.v1.api_info.sales_tax_rates.PutSalesTaxRatesByAddressApiInfo;
import com.complyt.v1.handler.ComplytSalesTaxRatesHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ComplytSalesTaxRatesRouter {

    public static final String BASE_URL = "/v1/sales_tax_rates";

    @Bean
    @GetSalesTaxRatesByAddressApiInfo
    public RouterFunction<ServerResponse> getComplytSalesTaxRatesByAddress(@NonNull final ComplytSalesTaxRatesHandler complytSalesTaxRatesHandler) {
        RequestPredicate getSalesTaxRatesRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSalesTaxRatesRoute, complytSalesTaxRatesHandler::getSalesTaxRatesByAddress);
    }

    @Bean
    @PutSalesTaxRatesByAddressApiInfo
    public RouterFunction<ServerResponse> putComplytSalesTaxRatesByAddress(@NonNull final ComplytSalesTaxRatesHandler complytSalesTaxRatesHandler) {
        RequestPredicate getSalesTaxRatesRoute = RequestPredicates
                .PUT(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSalesTaxRatesRoute, complytSalesTaxRatesHandler::putSalesTaxRatesByAddress);
    }


}