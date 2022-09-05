package com.complyt;

import com.complyt.annotations.Generated;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Generated
@SpringBootApplication
public class ComplytApplication implements CommandLineRunner {
    // diff
    public static void main(String[] args) {
        SpringApplication.run(ComplytApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
