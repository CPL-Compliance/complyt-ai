package io.complyt.authentication.v1.routers;

import io.complyt.authentication.v1.handlers.PartnershipHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;


@Configuration
public class PartnershipRouter {
    public static final String BASE_URL = "/v1/partnership";

    @Bean
    public RouterFunction<ServerResponse> getPartnership(@NonNull final PartnershipHandler partnershipHandler) {
        RequestPredicate postTokenRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));

        return RouterFunctions.route(postTokenRoute, partnershipHandler::getPartnership);
    }

    @Bean
    public RouterFunction<ServerResponse> upsertReferral(@NonNull final PartnershipHandler partnershipHandler) {
        RequestPredicate postTokenRoute = RequestPredicates
                .POST(BASE_URL + "/client")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));

        return RouterFunctions.route(postTokenRoute, partnershipHandler::upsertReferral);
    }

    @Bean
    public RouterFunction<ServerResponse> deleteReferral(@NonNull final PartnershipHandler partnershipHandler) {
        RequestPredicate postTokenRoute = RequestPredicates
                .DELETE(BASE_URL + "/client")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));

        return RouterFunctions.route(postTokenRoute, partnershipHandler::deleteReferral);
    }
}
