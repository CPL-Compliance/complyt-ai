package com.complyt.v1.router;

import com.complyt.v1.handler.CountyHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class CountyRouter {

    public static final String BASE_URL = "/v1/county";

    @Bean
    public RouterFunction<ServerResponse> getCountyByAddress(@NonNull final CountyHandler countyHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, countyHandler::getCountyByAddress);
    }

}
