package io.complyt.files.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
public class LocaleConfig {

    @PostConstruct
    void setUTCTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
