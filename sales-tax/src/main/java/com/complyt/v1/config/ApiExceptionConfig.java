package com.complyt.v1.config;

import com.complyt.annotations.Generated;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.MissingBodyApiException;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Configuration
@Generated
public class ApiExceptionConfig {

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }

    @Bean
    public HttpStatus defaultStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    @Bean
    public Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode() {
        return Map.of(
                ObjectNotFoundApiException.class, HttpStatus.NOT_FOUND,
                ObjectNotValidApiException.class, HttpStatus.BAD_REQUEST,
                MissingBodyApiException.class, HttpStatus.BAD_REQUEST,
                ConflictedDataApiException.class, HttpStatus.BAD_REQUEST
        );
    }
}
