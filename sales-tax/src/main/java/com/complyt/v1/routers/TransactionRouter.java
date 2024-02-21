package com.complyt.v1.routers;

import com.complyt.v1.api_info.transaction.*;
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
    @GetTransactionByExternalIdAndSourceApiInfo
    public RouterFunction<ServerResponse> getTransactionByExternalIdAndSourceRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL + "/source/{source}/externalId/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, transactionHandler::getByExternalIdAndSource);
    }

    @Bean
    @GetAllTransactionsApiInfo
    public RouterFunction<ServerResponse> getAllTransactionsRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, transactionHandler::getAll);
    }

    @Bean
    @GetAllTransactionsBySourceApiInfo
    public RouterFunction<ServerResponse> getAllTransactionsBySourceRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL + "/source/{source}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, transactionHandler::getAllBySource);
    }

    @Bean
    @GetTransactionByComplytIdApiInfo
    public RouterFunction<ServerResponse> getTransactionByComplytIdRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate getTransactionRoute = RequestPredicates
                .GET(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getTransactionRoute, transactionHandler::getByComplytId);
    }

    @Bean
    @UpsertTransactionByExternalIdAndSourceApiInfo
    public RouterFunction<ServerResponse> upsertTransactionRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate putTransactionRoute = RequestPredicates
                .PUT(BASE_URL + "/source/{source}/externalId/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(putTransactionRoute, transactionHandler::upsert);
    }

    @Bean
    @DeleteTransactionByExternalIdAndSourceApiInfo
    public RouterFunction<ServerResponse> deleteTransactionRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate deleteTransactionRoute = RequestPredicates
                .DELETE(BASE_URL + "/source/{source}/externalId/{externalId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(deleteTransactionRoute, transactionHandler::delete);
    }

    @Bean
//    @PatchTransactionByComplytIdApiInfo
    public RouterFunction<ServerResponse> patchTransactionRouterFunction(@NonNull final TransactionHandler transactionHandler) {
        RequestPredicate deleteTransactionRoute = RequestPredicates
                .PATCH(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(deleteTransactionRoute, transactionHandler::patch);
    }
    
}