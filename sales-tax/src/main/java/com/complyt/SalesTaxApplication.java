package com.complyt;

import com.complyt.annotations.Generated;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import reactivefeign.spring.config.EnableReactiveFeignClients;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Generated
@SpringBootApplication
@EnableReactiveFeignClients
@EnableFeignClients
public class SalesTaxApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SalesTaxApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}