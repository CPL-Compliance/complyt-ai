package io.complyt.filing.v1.router;

import io.complyt.filing.v1.handler.LinkHandler;
import lombok.Generated;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@RequiredArgsConstructor
@Configuration
@Generated
public class LinkRouter {
    @NonNull
    final LinkHandler linkHandler;
    public static final String BASE_URL = "/v1/links";

    @Bean
    public RouterFunction<ServerResponse> exemptionsRoute() {
        return RouterFunctions.route()
                .path(BASE_URL, builder -> builder
                        .GET("", linkHandler::getAll))
                .build();
    }
}
