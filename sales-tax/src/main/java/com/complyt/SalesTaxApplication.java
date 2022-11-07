package com.complyt;

import com.complyt.annotations.Generated;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@Generated
//@OpenAPIDefinition(
//        servers = {
//                @Server(url = "/", description = "Default Server URL")
//        }
//)
@SpringBootApplication
public class SalesTaxApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(SalesTaxApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
