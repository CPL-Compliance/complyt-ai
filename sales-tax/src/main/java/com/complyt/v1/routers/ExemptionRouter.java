package com.complyt.v1.routers;

import com.complyt.v1.api_info.exemption.*;
import com.complyt.v1.handlers.ExemptionHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ExemptionRouter {

    public static final String BASE_URL = "/v1/exemptions";

    @Bean
    @GetExemptionByComplytIdApiInfo
    public RouterFunction<ServerResponse> GetExemptionByComplytIdRouterFunction(@NonNull final ExemptionHandler exemptionHandler) {
        RequestPredicate getExemptionRoute = RequestPredicates
                .GET(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getExemptionRoute, exemptionHandler::findByComplytId);
    }

    @Bean
    @GetAllExemptionsApiInfo
    public RouterFunction<ServerResponse> GetAllExemptionsRouterFunction(@NonNull final ExemptionHandler exemptionHandler) {
        RequestPredicate getAllExemptiosRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getAllExemptiosRoute, exemptionHandler::getAll);
    }

    @Bean
    public RouterFunction<ServerResponse> postExemptionRouterFunction(@NonNull final ExemptionHandler exemptionHandler) {
        RequestPredicate postExemptionRoute = RequestPredicates
                .POST(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(postExemptionRoute, exemptionHandler::create);
    }

    @Bean
    @PutExemptionApiInfo
    public RouterFunction<ServerResponse> updateExemptionByComplytIdRouterFunction(@NonNull final ExemptionHandler exemptionHandler) {
        RequestPredicate putExemptionRoute = RequestPredicates
                .PUT(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putExemptionRoute, exemptionHandler::update);
    }

    @Bean
    @DeleteExemptionByComplytIdApiInfo
    public RouterFunction<ServerResponse> deleteExemptionByComplytIdRouterFunction(@NonNull final ExemptionHandler exemptionHandler) {
        RequestPredicate deleteExemptionRoute = RequestPredicates
                .DELETE(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(deleteExemptionRoute, exemptionHandler::delete);
    }

    @Bean
    @PatchExemptionByComplytIdApiInfo
    public RouterFunction<ServerResponse> patchExemptionRouterFunction(@NonNull final ExemptionHandler exemptionHandler) {
        RequestPredicate deleteExemptionRoute = RequestPredicates
                .PATCH(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(deleteExemptionRoute, exemptionHandler::patch);
    }

}