package com.complyt.v1.controllers.router;

import com.complyt.facades.ExemptionFacade;
import com.complyt.v1.controllers.router.handler.ExemptionHandler;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Configuration
@AllArgsConstructor
public class ExemptionRouter {

    public static final String BASE_URL = "/v1/exemptions";

    @Bean
    public RouterFunction<?> exemptionsRoute(ExemptionHandler exemptionHandler) {
        return route()
                .nest(path(BASE_URL), builder -> builder
                        .GET("/{id}", exemptionHandler::getOne)
                        .GET("", exemptionHandler::getAll)
                        .POST("", exemptionHandler::create)
                        .PUT("/{id}", exemptionHandler::update))
                .build();
    }
}
