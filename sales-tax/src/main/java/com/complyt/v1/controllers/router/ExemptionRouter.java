package com.complyt.v1.controllers.router;

import com.complyt.v1.controllers.router.handler.ExemptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ExemptionRouter {

    @Bean
    public RouterFunction<ServerResponse> exemptionsRoute(ExemptionHandler exemptionHandler) {
        return route()
                .nest(path("/v1/exemptions"), builder -> builder
                        .GET("", exemptionHandler::getAll)
                        .GET("/{id}", exemptionHandler::getOne)
                        .POST("", exemptionHandler::create)
                        .PUT("/{id}", exemptionHandler::update))
                .build();

    }
}
