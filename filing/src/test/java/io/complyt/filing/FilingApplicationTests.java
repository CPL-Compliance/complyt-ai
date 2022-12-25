package io.complyt.filing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpringSecurityTestConfiguration {

}
@SpringBootTest(classes = SpringSecurityTestConfiguration.class)
class FilingApplicationTests {

    @Test
    void contextLoads() {
    }

}
