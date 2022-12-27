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

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;


@Configuration
@Generated
public class LinkRouter {
    public static final String BASE_URL = "/v1/links";

    @Bean
    public RouterFunction<ServerResponse> exemptionsRoute(@NonNull final LinkHandler linkHandler) {
        return RouterFunctions.route()
                .GET(BASE_URL, accept(APPLICATION_JSON), linkHandler::getAll)
                .build();
    }
}
