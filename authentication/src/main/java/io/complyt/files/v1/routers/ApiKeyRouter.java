package io.complyt.files.v1.routers;

import io.complyt.files.v1.api_info.GetLinkApiInfo;
import io.complyt.files.v1.handlers.FileHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;


@Configuration
public class ApiKeyRouter {
    public static final String BASE_URL = "/v1/files";

    @Bean
    @GetLinkApiInfo
    public RouterFunction<ServerResponse> getfileLinkRouterFunction(@NonNull final FileHandler fileHandler) {
        RequestPredicate getFileLinkRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getFileLinkRoute, fileHandler::get);
    }
}
