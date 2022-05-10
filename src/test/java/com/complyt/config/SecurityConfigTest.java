package com.complyt.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityConfigTest {

    @InjectMocks
    SecurityConfig securityConfig;

    @Mock
    ServerHttpSecurity http;

    @Test
    void securityFilterChain_CreatesChain_ReturnsChain(){
//        // Given
//        SecurityWebFilterChain securityWebFilterChain = http
//                .csrf()
//                .disable()
//                .authorizeExchange()
//                .pathMatchers("/").permitAll()
//                .anyExchange().authenticated()
//                .and()
//                .httpBasic()
//                .and()
//                .formLogin().disable()
//                .build();
//
//        SecurityWebFilterChain returnedSecurityWebFilterChain = securityConfig.securityFilterChain(http);

//        // Then
//        Assertions.assertEquals(securityWebFilterChain,returnedSecurityWebFilterChain);
    }

    @Test
    void userDetailsService_BuildUser_ReturnReactiveUserDetailService(){
//        UserDetails user = User.builder()
//                .username("admin")
//                .password("{noop}admin")
//                .roles("bwr")
//                .build();
//        MapReactiveUserDetailsService mapReactiveUserDetailsService = new MapReactiveUserDetailsService(user);
//
//        MapReactiveUserDetailsService secondMapReactiveUserDetailsService = securityConfig.userDetailsService();
//        Assertions.assertEquals(mapReactiveUserDetailsService,secondMapReactiveUserDetailsService);
    }

}
