package io.complyt.authentication.v1.routers;

import io.complyt.authentication.v1.handlers.SecretKeyHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;


@Configuration
public class SecretKeyRouter {
    public static final String BASE_URL = "/v1/secret_key";

    @Bean
    public RouterFunction<ServerResponse> getSecretKeyRouterFunction(@NonNull final SecretKeyHandler secretKeyHandler) {
        RequestPredicate getSecretKeyRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSecretKeyRoute, secretKeyHandler::get);
    }
}
