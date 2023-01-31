package com.complyt.v1.routers;

import com.complyt.v1.handlers.TransactionHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class TransactionRouter {

    public static final String BASE_URL = "/v1/transactions";

    @Bean
    public RouterFunction<ServerResponse> getTransactionByExternalIdRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL + "/source/{source}/externalId/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, transactionHandler::getByExternalIdAndSource);
    }
}