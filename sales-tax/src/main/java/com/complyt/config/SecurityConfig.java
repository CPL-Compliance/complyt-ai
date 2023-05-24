package com.complyt.config;

import com.complyt.annotations.Generated;
import com.complyt.security.AudienceValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Configuration
@Generated
public class SecurityConfig {

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Bean
    @Profile({"production", "development", "demo", "test", "default"})
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
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/actuator/**").hasAuthority("SCOPE_read:actuator")
                .anyExchange().authenticated();

        // OAuth2
        http.oauth2ResourceServer().jwt();

        return http.build();
    }

    @Profile({"development", "demo", "test", "default"})
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

    @Profile({"integration-test"})
    @Bean
    public SecurityWebFilterChain integrationTestSecurityWebFilterChain(ServerHttpSecurity http) {
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

    //
//    @Profile({"integration-test"})
//    @Bean
//    public MapReactiveUserDetailsService userDetailsService() {
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("mock-user")
//                .password("integration-test")
//                .roles("USER")
//                .authorities("SCOPE_create:customer", "SCOPE_delete:customer", "SCOPE_read:customer ",
//                        "SCOPE_update:customer", "SCOPE_create:transaction", "SCOPE_read:transaction",
//                        "SCOPE_update:transaction", "SCOPE_delete:transaction", "SCOPE_read:state",
//                        "SCOPE_create:exemption", "SCOPE_update:exemption", "SCOPE_delete:exemption",
//                        "SCOPE_read:exemption", "SCOPE_create:nexus", "SCOPE_read:nexus", "SCOPE_delete:nexus", "SCOPE_update:nexus", "SCOPE_read:link")
//                .build();
//        return new MapReactiveUserDetailsService(user);
//    }
    @Bean
    @Profile({"integration-test"})
    JwtDecoder integrationTestJwtDecoder() {
        /*
        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
        indeed intended for our app. Adding our own validator is easy to do:
        */
        return token -> Jwt.withTokenValue("token")
                .header("typ", "JWT")
                .issuer("https://localhost")
                .claim("tenant_id", "it_tenant")
                .claim("scope", "create:customer delete:customer read:customer " +
                        "update:customer create:transaction read:transaction " +
                        "update:transaction delete:transaction read:state " +
                        "create:exemption update:exemption delete:exemption " +
                        "read:exemption create:nexus read:nexus delete:nexus update:nexus read:link").build();
    }

}