package testUtils.annotations;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WithOAuth2TokenMockUserSecurityContextFactory
        implements WithSecurityContextFactory<WithOAuth2TokenMockUser> {

    @Override
    public SecurityContext createSecurityContext(WithOAuth2TokenMockUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // Set authorities
        List<SimpleGrantedAuthority> authorities = Arrays.stream(annotation.roles())
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());

        // Create a mock Jwt token with desired claims
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("sub", annotation.username().isEmpty() ? annotation.value() : annotation.username())
                .claim("authorities", authorities.stream()
                        .map(SimpleGrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .claim("tenant_id", "tenant_123")  // Add tenant_id claim here
                .build();

        // Use JwtAuthenticationToken or another suitable AbstractOAuth2TokenAuthenticationToken subclass
        JwtAuthenticationToken authToken = new JwtAuthenticationToken(jwt, authorities);

        context.setAuthentication(authToken);
        return context;
    }
}
