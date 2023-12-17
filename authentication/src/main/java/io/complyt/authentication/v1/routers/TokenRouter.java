package io.complyt.authentication.v1.routers;

import io.complyt.authentication.v1.api_info.PostTokenApiInfo;
import io.complyt.authentication.v1.handlers.TokenHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;


@Configuration
public class TokenRouter {
    public static final String BASE_URL = "/v1/token";

    @Bean
    @PostTokenApiInfo
    public RouterFunction<ServerResponse> postTokenRouterFunction(@NonNull final TokenHandler tokenHandler) {
        RequestPredicate postTokenRoute = RequestPredicates
                .POST(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON, MediaType.APPLICATION_FORM_URLENCODED));

        return RouterFunctions.route(postTokenRoute, tokenHandler::post);
    }
}
