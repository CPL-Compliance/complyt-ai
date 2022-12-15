package com.complyt.security;

import com.complyt.annotations.Generated;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import reactivefeign.client.ReactiveHttpRequest;
import reactivefeign.client.ReactiveHttpRequestInterceptor;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Component
@Generated
class BearerTokenRequestInterceptor implements ReactiveHttpRequestInterceptor {

    @Override
    public Mono<ReactiveHttpRequest> apply(ReactiveHttpRequest reactiveHttpRequest) {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    final String authorization = HttpHeaders.AUTHORIZATION;

                    if (securityContext.getAuthentication() instanceof JwtAuthenticationToken) {
                        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) securityContext.getAuthentication();
                        String tokenValue = jwtAuthenticationToken.getToken().getTokenValue();
                        List<String> list = new ArrayList<>();
                        list.add("Bearer " + tokenValue);
                        reactiveHttpRequest.headers().put(authorization, list);
                    }

                    return reactiveHttpRequest;
                });
    }
}