package com.complyt;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.LocalDateTime;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
@SpringBootApplication
public class ComplytApplication implements CommandLineRunner {

    public static void main(String[] args) {
        System.out.println(LocalDateTime.now().minusYears(2).with(firstDayOfYear()).withHour(0).withMinute(0).withSecond(0).withNano(0));
        SpringApplication.run(ComplytApplication.class, args);
    }

    @Override
    public void run(String... args) {
    }
}
