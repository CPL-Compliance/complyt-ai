package io.complyt.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Configuration
class SpringSecurityTestConfiguration {

}

// I use SpringSecurityTestConfiguration to prevent SpringBootTest from load the production configuration files.
@SpringBootTest(classes = SpringSecurityTestConfiguration.class)
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
    }
}