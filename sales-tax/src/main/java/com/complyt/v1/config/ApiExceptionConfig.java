package com.complyt.v1.config;

import com.complyt.annotations.Generated;
import com.complyt.v1.exceptions.types.*;
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
        return Map.ofEntries(
                Map.entry(ObjectNotFoundApiException.class, HttpStatus.NOT_FOUND),
                Map.entry(CurrencyNotFoundApiException.class, HttpStatus.UNPROCESSABLE_ENTITY),
                Map.entry(ObjectNotValidApiException.class, HttpStatus.BAD_REQUEST),
                Map.entry(MissingBodyApiException.class, HttpStatus.BAD_REQUEST),
                Map.entry(ConflictedDataApiException.class, HttpStatus.BAD_REQUEST),
                Map.entry(PathVariableErrorException.class, HttpStatus.BAD_REQUEST),
                Map.entry(QueryParamErrorException.class, HttpStatus.BAD_REQUEST),
                Map.entry(InvalidPatchFieldException.class, HttpStatus.BAD_REQUEST),
                Map.entry(InvalidDiscountAmountException.class, HttpStatus.BAD_REQUEST),
                Map.entry(TaxCodeNotValidException.class, HttpStatus.BAD_REQUEST),
                Map.entry(CountryNotFoundInJurisdictionalTaxRulesApiException.class, HttpStatus.BAD_REQUEST),
                Map.entry(StateNotFoundInJurisdictionalTaxRulesApiException.class, HttpStatus.BAD_REQUEST),
                Map.entry(CustomerNotFoundApiException.class, HttpStatus.NOT_FOUND),
                Map.entry(ZipCodeNotFoundApiException.class, HttpStatus.BAD_REQUEST),
                Map.entry(ZipCodeNotValidApiException.class, HttpStatus.BAD_REQUEST),
                Map.entry(InvalidLocalDateTimeFormatException.class, HttpStatus.BAD_REQUEST)
        );
    }
}
