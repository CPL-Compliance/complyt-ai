package io.complyt.testService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@Configuration
class SpringSecurityTestConfiguration {

}

@SpringBootTest(classes = SpringSecurityTestConfiguration.class)
class TestServiceApplicationTest {
	@Test
	void contextLoads() {
	}

}
