package com.complyt.v1.routers;

import com.complyt.v1.api_info.vat_validation.GetVatValidationApiInfo;
import com.complyt.v1.handlers.VatValidationHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class VatValidationRouter {
    public static final String BASE_URL = "/v1/vat";

    @Bean
    @GetVatValidationApiInfo
    public RouterFunction<ServerResponse> getValidatedVat(@NonNull final VatValidationHandler vatValidationHandler) {
        RequestPredicate getSalesTaxTrackingRoute = RequestPredicates
                .GET(BASE_URL + "/validate")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSalesTaxTrackingRoute, vatValidationHandler::validatedVat);
    }
}