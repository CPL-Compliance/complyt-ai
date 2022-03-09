package com.complyt;

import com.complyt.entity.State;
import com.complyt.repository.StateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootApplication
@EnableMongoRepositories("com.complyt.repository")
public class ComplytApplication  implements CommandLineRunner {

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Autowired
	StateRepository stateRepository;

	public static void main(String[] args) {

		SpringApplication.run(ComplytApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

	}
}
