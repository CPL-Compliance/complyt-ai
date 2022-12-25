package io.complyt.filing.v1.router;

import io.complyt.filing.v1.handler.FilingHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class FilingRouter {

    public static final String BASE_URL = "/v1/filing";

    @Bean
    public RouterFunction<ServerResponse> exemptionsRoute(@NonNull final FilingHandler filingHandler) {
        return RouterFunctions.route()
                .path(BASE_URL, builder -> builder
                        .GET("", filingHandler::getOne))
                .build();
    }
}
