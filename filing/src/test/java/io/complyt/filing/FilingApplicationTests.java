package io.complyt.filing;

import io.complyt.filing.config.SpringSecurityTestConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;


@SpringBootTest(classes = SpringSecurityTestConfiguration.class)
class FilingApplicationTests {

    @Test
    void contextLoads() {
    }

}
