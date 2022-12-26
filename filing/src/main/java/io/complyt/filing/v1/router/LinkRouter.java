package io.complyt.filing.v1.router;

import io.complyt.filing.v1.handler.LinkHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class LinkRouter {

    public static final String BASE_URL = "/v1/links";

    @Bean
    public RouterFunction<ServerResponse> exemptionsRoute(@NonNull final LinkHandler linkHandler) {
        return RouterFunctions.route()
                .path(BASE_URL, builder -> builder
                        .GET("", linkHandler::getAll))
                .build();
    }
}
