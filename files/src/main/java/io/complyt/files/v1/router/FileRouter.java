package io.complyt.files.v1.router;

import io.complyt.files.v1.handler.FileHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class FileRouter {
    public static final String BASE_URL = "/v1/files";

    @Bean
    public RouterFunction<ServerResponse> fileRoute(@NonNull final FileHandler fileHandler) {
        return RouterFunctions.route()
                .GET(BASE_URL, accept(APPLICATION_JSON), fileHandler::getAll)
                .build();
    }
}
