package com.complyt.v1.routers;

import com.complyt.v1.handlers.ClientTrackingHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ClientTrackingRouter {

    public static final String BASE_URL = "/v1/clientTracking";

    @Bean
    public RouterFunction<ServerResponse> getAll(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate putClientTrackingRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putClientTrackingRoute, clientTrackingHandler::getAll);
    }

    @Bean
    public RouterFunction<ServerResponse> getByName(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate putClientTrackingRoute = RequestPredicates
                .GET(BASE_URL + "/name/{name}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putClientTrackingRoute, clientTrackingHandler::getByName);
    }

    @Bean
    public RouterFunction<ServerResponse> getByTenantId(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate putClientTrackingRoute = RequestPredicates
                .GET(BASE_URL + "/tenantId/{tenantId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putClientTrackingRoute, clientTrackingHandler::getByTenantId);
    }

    @Bean
    public RouterFunction<ServerResponse> upsert(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate putClientTrackingRoute = RequestPredicates
                .PUT(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putClientTrackingRoute, clientTrackingHandler::upsert);
    }

}
