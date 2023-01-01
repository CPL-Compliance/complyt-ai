package io.complyt.files;

import io.complyt.files.annotations.Generated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
@Generated
public class FilesApplication {
    public static void main(String[] args) {
        SpringApplication.run(FilesApplication.class, args);
    }
}
