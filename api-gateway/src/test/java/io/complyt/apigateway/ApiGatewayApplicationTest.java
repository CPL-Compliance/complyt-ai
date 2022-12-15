package io.complyt.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpringSecurityTestConfiguration {

}

// I use SpringSecurityTestConfiguration to prevent SpringBootTest from loading the production configuration files.
@SpringBootTest(classes = SpringSecurityTestConfiguration.class)
class ApiGatewayApplicationTest {

    @Test
    void contextLoads() {
    }
}