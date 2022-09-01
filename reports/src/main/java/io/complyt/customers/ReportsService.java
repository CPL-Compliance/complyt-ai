package io.complyt.customers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class ReportsService implements CommandLineRunner {
	// This is a test
	public static void main(String[] args) {
		SpringApplication.run(ReportsService.class, args);
	}

	@Override
	public void run(String... args) {
	}
}