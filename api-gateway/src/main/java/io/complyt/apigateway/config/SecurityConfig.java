package io.complyt.apigateway.config;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import io.complyt.apigateway.annotations.Generated;
import io.complyt.apigateway.security.AudienceValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Generated
@Configuration
public class SecurityConfig {

    @Value("${auth0.audience}")
    private String audience;

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Profile({"development", "demo", "test", "default", "production"})
    @Bean
    JwtDecoder productionJwtDecoder() {
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
                        "/files/v3/api-docs",
                        "/sales-tax/v3/api-docs",
                        "/sales-tax-rates/v3/api-docs",
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

//    @Profile({"integration-test"})
//    @Bean
//    public SecurityWebFilterChain integrationTestSecurityWebFilterChain(ServerHttpSecurity http) {
//
//        http.csrf().disable();
//        // Authentication and Authorization
//        http.authorizeExchange()
//                .anyExchange().permitAll();
//
//        http.anonymous();
//
//        http.httpBasic().disable()
//                .formLogin().disable();
//
//        //http.oauth2ResourceServer().jwt();
//        return http.build();
//    }

//    @Bean
//    @Profile({"integration-test"})
//    ReactiveJwtDecoder integrationTestJwtDecoder() {
//        /*
//        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
//        indeed intended for our app. Adding our own validator is easy to do:
//        */
//        return new ReactiveJwtDecoder() {
//            @Override
//            public Mono<Jwt> decode(String token) throws JwtException {
//                return Mono.just(Jwt.withTokenValue("token")
//                        .header("typ", "JWT")
//                        .issuer("https://localhost")
//                        .claim("tenant_id", "it_tenant")
//                        .claim("scope", "create:customer delete:customer read:customer " +
//                                "update:customer create:transaction read:transaction " +
//                                "update:transaction delete:transaction read:state " +
//                                "create:exemption update:exemption delete:exemption " +
//                                "read:exemption create:nexus read:nexus delete:nexus update:nexus read:link").build());
//            }
//        };
//    }

//    @Bean
//    @Profile({"integration-test"})
//    JwtDecoder integrationTestJwtDecoder2() {
//        /*
//        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
//        indeed intended for our app. Adding our own validator is easy to do:
//        */
//        return new JwtDecoder() {
//            @Override
//            public Jwt decode(String token) throws JwtException {
//                return Jwt.withTokenValue("token")
//                        .header("typ", "JWT")
//                        .issuer("https://localhost")
//                        .claim("tenant_id", "it_tenant")
//                        .claim("scope", "create:customer delete:customer read:customer " +
//                                "update:customer create:transaction read:transaction " +
//                                "update:transaction delete:transaction read:state " +
//                                "create:exemption update:exemption delete:exemption " +
//                                "read:exemption create:nexus read:nexus delete:nexus update:nexus read:link").build();
//            }
//        };
//    }

    @Profile({"integration-test"})
    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.authorizeExchange().anyExchange().permitAll();
        http.oauth2ResourceServer().jwt();
        return http.build();
    }

//    @Profile({"integration-test"})
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverterForKeycloak() {
//        Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter = jwt -> {
//            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
//
//            Object client = resourceAccess.get("integration-test-client");
//
//            LinkedTreeMap<String, List<String>> clientRoleMap = (LinkedTreeMap<String, List<String>>) client;
//
//            List<String> clientRoles = new ArrayList<>(clientRoleMap.get("roles"));
//
//            return clientRoles.stream()
//                    .map(SimpleGrantedAuthority::new)
//                    .collect(Collectors.toList());
//        };
//
//        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
//
//        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
//
//        return jwtAuthenticationConverter;
//    }
}