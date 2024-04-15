package com.complyt.v1.router;

import com.complyt.v1.api_info.sales_tax_rates.GetGtRatesByAddressApiInfo;
import com.complyt.v1.handler.ComplytGtRatesHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class GtRatesRouter {

    public static final String BASE_URL = "/v1/gt_rates";

    @Bean
    @GetGtRatesByAddressApiInfo
    public RouterFunction<ServerResponse> getGstRatesByAddress(@NonNull final ComplytGtRatesHandler complytGtRatesHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, complytGtRatesHandler::getGtRatesByAddress);
    }

}
