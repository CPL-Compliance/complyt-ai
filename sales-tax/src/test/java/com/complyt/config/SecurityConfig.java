package com.complyt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

@Configuration
public class SecurityConfig {
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        // CORS
//
//        // CSRF
//        http.csrf().requireCsrfProtectionMatcher(serverWebExchange -> ServerWebExchangeMatchers
//                .pathMatchers("/token/**")
//                .matches(serverWebExchange));
//
//        // Authentication and Authorization
//        http.authorizeExchange()
//                .pathMatchers("/actuator/health",
//                        "/v3/api-docs/**",
//                        "/webjars/**",
//                        "/swagger-ui*/**"
//                ).permitAll()
//                .pathMatchers("/actuator/**").hasAuthority("SCOPE_read:actuator")
//                .anyExchange().authenticated();
//
//        // OAuth2
////        http.oauth2ResourceServer().jwt();
//
//        return http.build();
//    }
}
