package io.complyt.v1.routers;

import io.complyt.v1.api_info.GetComplytResolveAddressByAddressApiInfo;
import io.complyt.v1.api_info.GetComplytValidatedAddressByAddressApiInfo;
import io.complyt.v1.handlers.ValidAddressHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class AddressRouter {
    public static final String BASE_URL = "/v1/addresses";

    @Bean
    @GetComplytValidatedAddressByAddressApiInfo
    public RouterFunction<ServerResponse> ValidateAddressByAddress(@NonNull final ValidAddressHandler validAddressHandler) {
        RequestPredicate getValidatedAddressRoute = RequestPredicates
                .GET(BASE_URL + "/validate")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(getValidatedAddressRoute, validAddressHandler::getValidAddressByAddress);
    }

    @Bean
    @GetComplytResolveAddressByAddressApiInfo
    public RouterFunction<ServerResponse> ResolveValidAddressByAddress(@NonNull final ValidAddressHandler validAddressHandler) {
        RequestPredicate getValidatedAddressRoute = RequestPredicates
                .GET(BASE_URL + "/resolve")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));
        return RouterFunctions.route(getValidatedAddressRoute, validAddressHandler::resolveValidAddressByAddress);
    }
}
