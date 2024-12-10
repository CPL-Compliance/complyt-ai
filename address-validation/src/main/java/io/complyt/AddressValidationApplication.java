package io.complyt;
import io.complyt.annotations.Generated;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Profile;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
@EnableFeignClients
@Generated
public class AddressValidationApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(AddressValidationApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}