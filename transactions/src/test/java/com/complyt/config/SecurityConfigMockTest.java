package com.complyt.config;

import com.complyt.security.UserDetailsServiceMock;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class SecurityConfigMockTest {
    @Bean
    public UserDetailsServiceMock userDetailsService() {
        return new UserDetailsServiceMock();
    }
}