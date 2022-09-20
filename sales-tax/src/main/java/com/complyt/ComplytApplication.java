package com.complyt;

import com.complyt.annotations.Generated;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@Generated
@SpringBootApplication
public class ComplytApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ComplytApplication.class, args);
    }

    @Value("${fast-tax-api-key}")
    String fastTaxKey;

    @Value("${zip-tax-api-key}")
    String zipTaxKey;

    @Override
    public void run(String... args) {
    }

    @PostConstruct
    private void postConstruct() {
        System.out.println("##########################");
        System.out.println(fastTaxKey);
        System.out.println(zipTaxKey);
        System.out.println("##########################");
    }
}
