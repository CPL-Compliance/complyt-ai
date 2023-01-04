package com.complyt.v1.controllers.routers;

import com.complyt.v1.controllers.handlers.CustomerHandler;
import com.complyt.v1.controllers.handlers.ExemptionHandler;
import lombok.NonNull;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class CustomerRouter {

    public static final String BASE_URL = "/v1/customers";

    @Bean
//    @RouterOperation(beanClass = CustomerHandler.class, beanMethod = "upsert")
    public RouterFunction<ServerResponse> customersRoute(@NonNull final CustomerHandler customerHandler) {
        return RouterFunctions.route()
                .path(BASE_URL, builder -> builder
                        .PUT("/{externalId}", customerHandler::upsert)
                        .GET("/{externalId}", customerHandler::getByExternalId))
//                        .POST("", customerHandler::create)
//                        .PUT("/{id}", customerHandler::update)
//                        .DELETE("/{id}", customerHandler::delete))
                .build();
    }
}