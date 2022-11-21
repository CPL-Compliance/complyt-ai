package com.complyt;

import com.complyt.annotations.Generated;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Generated
@SpringBootApplication
public class SalesTaxApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SalesTaxApplication.class, args);
    }

    @Override
    public void run(String... args) {

    }
}
