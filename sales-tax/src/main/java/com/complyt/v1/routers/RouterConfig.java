package com.complyt.v1.routers;

import com.complyt.v1.validators.AnnotatedRequestEntityValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> validationsRouter(@Autowired AnnotatedRequestEntityValidationHandler annotatedEntityHandler) {
        return RouterFunctions.route(RequestPredicates.POST("/annotated-functional-validation"), annotatedEntityHandler::handleRequest);
    }
}
