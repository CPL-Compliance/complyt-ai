package com.complyt.v1.controllers.routers;

import com.complyt.v1.controllers.api_info.customer.GetCustomerByExternalIdApiInfo;
import com.complyt.v1.controllers.handlers.CustomerHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class CustomerRouter {

    public static final String BASE_URL = "/v1/customers";

    @Bean
    public RouterFunction<ServerResponse> upsertCustomerByExternalIdRouterFunction(@NonNull final CustomerHandler customerHandler) {
        RequestPredicate putCustomerRoute = RequestPredicates
                .PUT(BASE_URL + "/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putCustomerRoute, customerHandler::upsert);
    }

    @Bean
    @GetCustomerByExternalIdApiInfo
    public RouterFunction<ServerResponse> getCustomerByExternalIdRouterFunction(@NonNull final CustomerHandler customerHandler) {
        RequestPredicate getCustomerRoute = RequestPredicates
                .GET(BASE_URL + "/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getCustomerRoute, customerHandler::getByExternalId);
    }
}