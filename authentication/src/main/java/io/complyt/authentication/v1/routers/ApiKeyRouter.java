package io.complyt.authentication.v1.routers;

import io.complyt.authentication.v1.handlers.ApiKeyHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class ApiKeyRouter {
    public static final String BASE_URL = "/v1/api_key";

    @Bean
    public RouterFunction<ServerResponse> postCredentialsRouterFunction(@NonNull final ApiKeyHandler apiKeyHandler) {
        RequestPredicate postApiKeyRoute = RequestPredicates
                .POST(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(postApiKeyRoute, apiKeyHandler::post);
    }

    @Bean
    public RouterFunction<ServerResponse> deleteCredentialsRouterFunction(@NonNull final ApiKeyHandler apiKeyHandler) {
        RequestPredicate postApiKeyRoute = RequestPredicates
                .DELETE(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));

        return RouterFunctions.route(postApiKeyRoute, apiKeyHandler::delete);
    }
}
