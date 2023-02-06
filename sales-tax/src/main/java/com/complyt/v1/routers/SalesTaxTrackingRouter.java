package com.complyt.v1.routers;

import com.complyt.v1.api_info.sales_tax_tracking.GetAllSalesTaxtrackingApiInfo;
import com.complyt.v1.api_info.sales_tax_tracking.GetSalesTaxTrackingByComplytIdApiInfo;
import com.complyt.v1.api_info.sales_tax_tracking.GetSalesTaxTrackingByStateApiInfo;
import com.complyt.v1.api_info.sales_tax_tracking.UpsertSalesTaxTrackingByStateApiInfo;
import com.complyt.v1.handlers.SalesTaxTrackingHandler;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;

@Configuration
public class SalesTaxTrackingRouter {

    public static final String BASE_URL = "/v1/nexus";

    @Bean
    @GetSalesTaxTrackingByStateApiInfo
    public RouterFunction<ServerResponse> getSalesTaxTrackingByStateRouterFunction(@NonNull final SalesTaxTrackingHandler salesTaxTrackingHandler) {
        RequestPredicate getSalesTaxTrackingRoute = RequestPredicates
                .GET(BASE_URL + "/state/{state}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSalesTaxTrackingRoute, salesTaxTrackingHandler::getOne);
    }

    @Bean
    @GetSalesTaxTrackingByComplytIdApiInfo
    public RouterFunction<ServerResponse> getSalesTaxTrackingByComplytIdRouterFunction(@NonNull final SalesTaxTrackingHandler salesTaxTrackingHandler) {
        RequestPredicate getSalesTaxTrackingRoute = RequestPredicates
                .GET(BASE_URL + "/complytId/{complytId}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSalesTaxTrackingRoute, salesTaxTrackingHandler::getByComplytId);
    }

    @Bean
    @GetAllSalesTaxtrackingApiInfo
    public RouterFunction<ServerResponse> getAllSalesTaxTrackingRouterFunction(@NonNull final SalesTaxTrackingHandler salesTaxTrackingHandler) {
        RequestPredicate getSalesTaxTrackingRoute = RequestPredicates
                .GET(BASE_URL)
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSalesTaxTrackingRoute, salesTaxTrackingHandler::getAll);
    }

    @Bean
    @UpsertSalesTaxTrackingByStateApiInfo
    public RouterFunction<ServerResponse> upsertSalesTaxTrackingRouterFunction(@NonNull final SalesTaxTrackingHandler salesTaxTrackingHandler) {
        RequestPredicate getSalesTaxTrackingRoute = RequestPredicates
                .PUT(BASE_URL + "/state/{state}")
                .and(RequestPredicates.accept(MediaType.APPLICATION_JSON));

        return RouterFunctions.route(getSalesTaxTrackingRoute, salesTaxTrackingHandler::upsert);
    }

}
