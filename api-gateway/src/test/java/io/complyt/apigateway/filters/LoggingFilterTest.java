package io.complyt.apigateway.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {
    @InjectMocks
    LoggingFilter loggingFilter;

    @Mock
    ServerWebExchange exchange;

    @Mock
    GatewayFilterChain chain;

    @Test
    void filter_loggingFilterExecutedDueToKnownRoute_ReactiveChainSucceeds() {
        MockServerHttpRequest mockServerHttpRequest = MockServerHttpRequest.get("/").build();

        when(exchange.getRequest()).thenReturn(mockServerHttpRequest);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> voidMono = loggingFilter.filter(exchange, chain);

        voidMono.doOnSuccess(unused -> System.out.println("Reactive chain succeeded")).subscribe();
    }
}