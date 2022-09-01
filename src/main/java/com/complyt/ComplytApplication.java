package com.complyt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class TransactionsService implements CommandLineRunner {
    // This is a test
    public static void main(String[] args) {
        SpringApplication.run(TransactionsService.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
