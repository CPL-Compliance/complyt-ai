package com.complyt.v1.controllers.router;

import com.complyt.v1.controllers.router.handler.ExemptionHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ExemptionRouter {

    public static final String BASE_URL = "/v1/exemptions";

    @Bean
    public RouterFunction<ServerResponse> exemptionsRoute(@NonNull final ExemptionHandler exemptionHandler) {
        return RouterFunctions.route()
                .path(BASE_URL, builder -> builder
                        .GET("/{id}", exemptionHandler::getOne)
                        .GET("", exemptionHandler::getAll)
                        .POST("", exemptionHandler::create)
                        .PUT("/{id}", exemptionHandler::update)
                        .DELETE("/{id}",exemptionHandler::delete))
                .build();
    }
}