package io.complyt.v1.config;

import io.complyt.utils.exceptions.types.ObjectNotValidException;
import io.complyt.utils.exceptions.types.ZipCodeMismatchException;
import io.complyt.utils.exceptions.types.fastTax.FastTaxException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Configuration
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
                ObjectNotValidException.class, HttpStatus.BAD_REQUEST,
                ZipCodeMismatchException.class, HttpStatus.BAD_REQUEST,
                FastTaxException.class, HttpStatus.BAD_REQUEST
        );
    }
}
