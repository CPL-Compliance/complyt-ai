package com.complyt.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TenantResolver {
    public Mono<String> resolve() {
        return ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> {
                    if (securityContext.getAuthentication() instanceof AbstractAuthenticationToken) {
                        AbstractOAuth2TokenAuthenticationToken token = (AbstractOAuth2TokenAuthenticationToken) securityContext.getAuthentication();

                        return (String) token.getTokenAttributes().get("tenant_id");
                    }

                    return "all";
                });
    }
}
