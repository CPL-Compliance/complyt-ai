package io.complyt.testService;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
class SpringSecurityTestConfiguration {

}

@SpringBootTest(classes = SpringSecurityTestConfiguration.class)
class TestServiceApplicationTest {
	@Test
	void contextLoads() {
	}

}
