package io.complyt.testService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
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
public class SecurityConfig {

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Bean
    JwtDecoder jwtDecoder() {
        /*
        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
        indeed intended for our app. Adding our own validator is easy to do:
        */
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
        http.authorizeExchange()
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/actuator/**").hasAuthority("SCOPE_read:actuator")
                .anyExchange().authenticated();

        // OAuth2
        http.oauth2ResourceServer().jwt();

        return http.build();
    }

    @Profile({"development", "penetration-test", "demo", "default"})
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
                        "/v3/api-docs/**",
                        "/webjars/**",
                        "/swagger-ui*/**"
                ).permitAll()
                .pathMatchers("/actuator/**").hasAuthority("SCOPE_read:actuator")
                .anyExchange().authenticated();

        // OAuth2
        http.oauth2ResourceServer().jwt();

        return http.build();
    }

}