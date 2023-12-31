package com.complyt.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class LocaleConfig {

    @PostConstruct
    public void setUTCTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}