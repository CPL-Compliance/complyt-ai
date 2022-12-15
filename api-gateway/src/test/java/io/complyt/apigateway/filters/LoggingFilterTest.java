package io.complyt.apigateway.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
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
    void filter() {
        MockServerHttpRequest mockServerHttpRequest = MockServerHttpRequest.get("/").build();

        when(exchange.getRequest()).thenReturn(mockServerHttpRequest);
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        assertEquals(Mono.empty(), loggingFilter.filter(exchange, chain));
    }
}