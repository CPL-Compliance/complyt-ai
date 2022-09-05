package io.complyt.testService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;

@SpringBootTest
class TestServiceApplicationTests {

	@Autowired

	TestServiceApplication testServiceApplication;

	@Test
	void contextLoads() {
		testServiceApplication.main(new ArrayList<String>());
	}

}
