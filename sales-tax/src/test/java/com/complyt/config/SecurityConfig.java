package com.complyt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig  //extends SecurityConfigurerAdapter
{

    @Profile({"integration-test"})
    @Bean
    public SecurityWebFilterChain integrationTestSecurityWebFilterChain(ServerHttpSecurity http) {

        // Authentication and Authorization
        http.authorizeExchange()
                .anyExchange().permitAll();
        http.anonymous();
        http.httpBasic().disable();
        //        .formLogin().disable();

        //http.oauth2ResourceServer().jwt();

        return http.build();
    }

//    @Bean
//    JwtDecoder jwtDecoder() {
//        /*
//        By default, Spring Security does not validate the "aud" claim of the token, to ensure that this token is
//        indeed intended for our app. Adding our own validator is easy to do:
//        */
//        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromOidcIssuerLocation("issuer");
//
//        OAuth2TokenValidator<Jwt> audienceValidator = new AudienceValidator("audience");
//        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer("issuer");
//        OAuth2TokenValidator<Jwt> withAudience = new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);
//
//        jwtDecoder.setJwtValidator(withAudience);
//
//        return jwtDecoder;
//    }

}
