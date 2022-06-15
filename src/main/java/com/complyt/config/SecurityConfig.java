package com.complyt.config;

import com.complyt.annotations.Generated;
import com.complyt.repositories.security.AuthorityRepository;
import com.complyt.repositories.security.RoleRepository;
import com.complyt.repositories.security.UserRepository;
import com.complyt.security.UserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@Generated
public class SecurityConfig {

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository,
                                          AuthorityRepository authorityRepository,
                                          RoleRepository roleRepository) {
        return new UserDetailsService(userRepository, authorityRepository, roleRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().requireCsrfProtectionMatcher(serverWebExchange -> ServerWebExchangeMatchers
                        .pathMatchers("/token/**")
                        .matches(serverWebExchange))
                .and()
                .authorizeExchange(authorize -> authorize
                        .pathMatchers("/login", "/logout", "/").permitAll()
                        .pathMatchers("/webjars/swagger-ui/index.html", "/swagger-ui.html").hasAuthority("swagger.read")
                        .anyExchange().authenticated())
                .httpBasic(withDefaults())
                .formLogin(withDefaults())
                .logout(withDefaults())
                .build();
    }
}