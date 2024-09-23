package io.complyt.files.v1.validators;

import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.FileDto;
import io.complyt.files.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
public class ValidatorConfig {

    @Bean
    ValidationHandler<FileDto, SpringValidatorAdapter> fileDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter) {
        return new ValidationHandler<>(FileDto.class, springValidatorAdapter, null);
    }

    @Bean
    ValidationHandler<ComplytFileDto, SpringValidatorAdapter> complytFileDtoValidationHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                              @Autowired QueryParamsExtractor<ComplytFileDto> queryParamsExtractor) {
        return new ValidationHandler<>(ComplytFileDto.class, springValidatorAdapter,
                queryParamsExtractor);
    }
}
