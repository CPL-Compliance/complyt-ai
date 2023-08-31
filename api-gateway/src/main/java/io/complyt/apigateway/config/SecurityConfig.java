package io.complyt.apigateway.config;

import io.complyt.apigateway.annotations.Generated;
import io.complyt.apigateway.security.AudienceValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Generated
@Configuration
public class SecurityConfig {

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Profile({"development", "demo", "test", "load-test", "default", "production"})
    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation(issuer);

        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator(audience);
        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuer);
        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        jwtDecoder.setJwtValidator(withAudience);

        return jwtDecoder;
    }

    @Profile({"production"})
    @Bean
    public SecurityWebFilterChain productionSecurityWebFilterChain(ServerHttpSecurity http) {
        // CORS

        // CSRF
        http.csrf().requireCsrfProtectionMatcher(serverWebExchange -> ServerWebExchangeMatchers
                .pathMatchers("/token/**")
                .matches(serverWebExchange));

        // Authentication and Authorization
        // We allow traffic to POST /v1/api_key to grant an access token to the user base on his scope.
        // This is part of our authentication process. We can't block this route because the user doesn't have
        // a token yet.
        http.authorizeExchange()
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers(HttpMethod.POST, "/v1/token").permitAll()
                .pathMatchers("/actuator/**").hasAuthority("SCOPE_read:actuator")
                .anyExchange().authenticated();

        // OAuth2
        http.oauth2ResourceServer().jwt();

        return http.build();
    }

    @Profile({"development", "demo", "test", "load-test", "default"})
    @Bean
    public SecurityWebFilterChain nonProductionSecurityWebFilterChain(ServerHttpSecurity http) {
        // CORS

        // CSRF
        http.csrf().requireCsrfProtectionMatcher(serverWebExchange -> ServerWebExchangeMatchers
                .pathMatchers("/token/**")
                .matches(serverWebExchange));

        // Authentication and Authorization
        http.authorizeExchange()
                .pathMatchers("/actuator/health",
                        "/files/v3/api-docs",
                        "/sales-tax/v3/api-docs",
                        "/sales-tax-rates/v3/api-docs",
                        "/authentication/v3/api-docs",
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/swagger-ui*/**"
                ).permitAll()
                .pathMatchers(HttpMethod.POST, "/v1/token").permitAll()
                .pathMatchers("/actuator/**").hasAuthority("SCOPE_read:actuator")
                .anyExchange().authenticated();

        // OAuth2
        http.oauth2ResourceServer().jwt();

        return http.build();
    }

    @Profile({"integration-test"})
    @Bean
    public SecurityWebFilterChain integrationTestFilterChain(ServerHttpSecurity http) {
        // Authentication and Authorization
        http.authorizeExchange()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers(HttpMethod.POST, "/v1/token").permitAll()
                .pathMatchers("/actuator/**").hasAuthority("SCOPE_read:actuator")
                .anyExchange().authenticated();

        // OAuth2
        http.oauth2ResourceServer().jwt();

        return http.build();
    }
}