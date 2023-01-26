package com.complyt.v1.routers;

import com.complyt.v1.api_info.customer.GetAllCustomersApiInfo;
import com.complyt.v1.api_info.customer.GetCustomerByExternalIdApiInfo;
import com.complyt.v1.api_info.customer.GetCustomerByNameApiInfo;
import com.complyt.v1.api_info.customer.UpsertCustomeByExternalIdApiInfo;
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
    @UpsertCustomeByExternalIdApiInfo
    public RouterFunction<ServerResponse> upsertCustomerByExternalIdRouterFunction(@NonNull final CustomerHandler customerHandler) {
        RequestPredicate putCustomerRoute = RequestPredicates
                .PUT(BASE_URL + "/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putCustomerRoute, customerHandler::upsert);
    }

    @Bean
    @GetAllCustomersApiInfo
    public RouterFunction<ServerResponse> getAllCustomersRouterFunction(@NonNull final CustomerHandler customerHandler) {
        RequestPredicate putCustomerRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putCustomerRoute, customerHandler::getAll);
    }

    @Bean
    @GetCustomerByExternalIdApiInfo
    public RouterFunction<ServerResponse> getCustomerByExternalIdRouterFunction(@NonNull final CustomerHandler customerHandler) {
        RequestPredicate getCustomerRoute = RequestPredicates
                .GET(BASE_URL + "/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getCustomerRoute, customerHandler::getByExternalId);
    }

    @Bean
    @GetCustomerByNameApiInfo
    public RouterFunction<ServerResponse> getCustomerByNameRouterFunction(@NonNull final CustomerHandler customerHandler) {
        RequestPredicate getCustomerRoute = RequestPredicates
                .GET(BASE_URL + "/name/{name}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getCustomerRoute, customerHandler::getByName);
    }
}