package com.complyt.v1.routers;

import com.complyt.v1.validators.CustomerValidationHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> customerValidationsRouter(@NonNull final CustomerValidationHandler customerValidationHandler) {
        return RouterFunctions.route(RequestPredicates.POST("/customerdto-validation/{externalId}"), customerValidationHandler::upsert);
    }
}
