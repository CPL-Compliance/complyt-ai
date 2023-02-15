package io.complyt.apigateway.filters;

import io.complyt.apigateway.utils.observability.ContextLogger;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String logStr = String.format("Request received:\nMethod -> " + exchange.getRequest().getMethod() + "\nPath -> "+ exchange.getRequest().getPath() + "\nBody - > " + exchange.getRequest().getBody());

        return ContextLogger.observeCtx(logStr, log::info).then(chain.filter(exchange));
    }
}