package com.complyt.v1.routers;

import com.complyt.v1.api_info.client_tracking.GetAllClientTrackingApiInfo;
import com.complyt.v1.api_info.client_tracking.GetClientTrackingByNameApiInfo;
import com.complyt.v1.api_info.client_tracking.GetClientTrackingByTenantIdApiInfo;
import com.complyt.v1.api_info.client_tracking.PutClientTrackingApiInfo;
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
    @GetAllClientTrackingApiInfo
    public RouterFunction<ServerResponse> getAll(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate getClientTrackingRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getClientTrackingRoute, clientTrackingHandler::getAll);
    }

    @Bean
    @GetClientTrackingByNameApiInfo
    public RouterFunction<ServerResponse> getByName(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate getClientTrackingRoute = RequestPredicates
                .GET(BASE_URL + "/name/{name}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getClientTrackingRoute, clientTrackingHandler::getByName);
    }

    @Bean
    @GetClientTrackingByTenantIdApiInfo
    public RouterFunction<ServerResponse> getByTenantId(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate getClientTrackingRoute = RequestPredicates
                .GET(BASE_URL + "/tenantId/{tenantId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getClientTrackingRoute, clientTrackingHandler::getByTenantId);
    }

    @Bean
    @PutClientTrackingApiInfo
    public RouterFunction<ServerResponse> upsert(@NonNull final ClientTrackingHandler clientTrackingHandler) {
        RequestPredicate putClientTrackingRoute = RequestPredicates
                .PUT(BASE_URL + "/tenantId/{tenantId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putClientTrackingRoute, clientTrackingHandler::upsert);
    }

}
