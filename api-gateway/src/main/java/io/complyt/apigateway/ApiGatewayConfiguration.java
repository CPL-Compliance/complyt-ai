package io.complyt.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

//    @Bean
//    public RouteLocator gatewayRouter(RouteLocatorBuilder routeLocatorBuilder){
//        return routeLocatorBuilder.routes()
//                .route(p -> p.path("/get")
//                        .filters(f -> f.addRequestHeader("Customer", "Alex")
//                                .addResponseHeader("backCustomer", "Raya"))
//                        .uri("http://httpbin.org:80"))
//                .route(p -> p.path("/sales-tax/**")
//                        .uri("lb://sales-tax"))
//                .build();
//    }
}
