package com.complyt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ComplytApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ComplytApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }
}
