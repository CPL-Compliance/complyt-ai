package com.complyt.v1.controllers.routers;

import com.complyt.v1.controllers.handlers.SalesTaxTrackingHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SalesTaxTrackingRouter {

    public static final String BASE_URL = "/v1/nexus";

    @Bean
    public RouterFunction<ServerResponse> salesTaxTrackingRoute(@NonNull final SalesTaxTrackingHandler salesTaxTrackingHandler) {
        return RouterFunctions.route()
                .path(BASE_URL, builder -> builder
                        .GET("/{state}", salesTaxTrackingHandler::getOne)
                        .GET("", salesTaxTrackingHandler::getAll)
                        .PUT("/{state}", salesTaxTrackingHandler::upsert))
                .build();
    }
}
