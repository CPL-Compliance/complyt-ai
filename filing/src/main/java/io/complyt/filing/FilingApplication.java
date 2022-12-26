package io.complyt.filing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class FilingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilingApplication.class, args);
    }
}
