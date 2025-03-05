package com.complyt.v1.config;

import com.complyt.annotations.Generated;
import com.complyt.v1.exceptions.types.*;
import com.complyt.v1.exceptions.types.fastTax.FastTaxException;
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
                ConflictedDataApiException.class, HttpStatus.BAD_REQUEST,
                PathVariableErrorException.class, HttpStatus.BAD_REQUEST,
                FastTaxException.class, HttpStatus.BAD_REQUEST
        );
    }
}
