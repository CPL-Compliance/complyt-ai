package com.complyt;

import com.complyt.annotations.Generated;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import reactivefeign.spring.config.EnableReactiveFeignClients;

import java.math.BigDecimal;

@Generated
@SpringBootApplication
@EnableReactiveFeignClients
@EnableFeignClients
public class SalesTaxApplication implements CommandLineRunner {

    public static void main(String[] args) {
        double x = 0.1, y=0.2;
        double z = x+y;
        System.out.println("z: " + z);
        BigDecimal number = new BigDecimal("0.01");
        BigDecimal number2 = new BigDecimal("0.02");
        BigDecimal sum = number.add(number2);
        System.out.println("sum: " + sum);
        SpringApplication.run(SalesTaxApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
