package com.complyt.v1.routers;

import com.complyt.v1.api_info.customer.*;
import com.complyt.v1.handlers.CustomerHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class CustomerRouter {

    public static final String BASE_URL = "/v1/customers";

    @Bean
    @UpsertCustomerByExternalIdAndSourceApiInfo
    public RouterFunction<ServerResponse> upsertCustomerByExternalIdRouterFunction( final CustomerHandler customerHandler) {
        RequestPredicate putCustomerRoute = RequestPredicates
                .PUT(BASE_URL + "/source/{source}/externalId/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putCustomerRoute, customerHandler::upsert);
    }

    @Bean
    @GetAllCustomersApiInfo
    public RouterFunction<ServerResponse> getAllCustomersRouterFunction( final CustomerHandler customerHandler) {
        RequestPredicate putCustomerRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putCustomerRoute, customerHandler::getAll);
    }

    @Bean
    @GetAllCustomersBySourceApiInfo
    public RouterFunction<ServerResponse> getAllCustomersBySourceRouterFunction( final CustomerHandler customerHandler) {
        RequestPredicate putCustomerRoute = RequestPredicates
                .GET(BASE_URL + "/source/{source}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putCustomerRoute, customerHandler::getAllBySource);
    }

    @Bean
    @GetCustomerByExternalIdAndSourceApiInfo
    public RouterFunction<ServerResponse> getCustomerByExternalIdRouterFunction( final CustomerHandler customerHandler) {
        RequestPredicate getCustomerRoute = RequestPredicates
                .GET(BASE_URL + "/source/{source}/externalId/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getCustomerRoute, customerHandler::getByExternalIdAndSource);
    }

    @Bean
    @GetCustomerByComplytIdApiInfo
    public RouterFunction<ServerResponse> getCustomerByComplytIdRouterFunction( final CustomerHandler customerHandler) {
        RequestPredicate getCustomerRoute = RequestPredicates
                .GET(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getCustomerRoute, customerHandler::getByComplytId);
    }

    @Bean
    @GetCustomerByNameApiInfo
    public RouterFunction<ServerResponse> getCustomerByNameRouterFunction(final CustomerHandler customerHandler) {
        RequestPredicate getCustomerRoute = RequestPredicates
                .GET(BASE_URL + "/name/{name}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getCustomerRoute, customerHandler::getByName);
    }
}