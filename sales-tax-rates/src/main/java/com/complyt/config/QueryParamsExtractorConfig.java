package com.complyt.config;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryParamsExtractorConfig {

    @Bean
    public QueryParamsExtractor<AddressDto> addressDtoQueryParamsExtractor() {
        return new AddressDtoQueryParamsExtractor();
    }

}
