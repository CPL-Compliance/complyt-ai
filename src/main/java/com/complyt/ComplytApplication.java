package com.complyt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@SpringBootApplication
public class ComplytApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ComplytApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
