package com.complyt;

import com.complyt.annotations.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@Generated
@SpringBootApplication
@EnableReactiveFeignClients
public class  SalesTaxRatesApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalesTaxRatesApplication.class, args);
    }
    
}