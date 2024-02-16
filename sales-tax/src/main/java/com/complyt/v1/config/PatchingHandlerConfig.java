package com.complyt.v1.config;

import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.validators.Patcher;
import com.complyt.v1.validators.PatchingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

@Configuration
public class PatchingHandlerConfig {

    @Bean
    PatchingHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoPatchingHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
                                                                                      @Autowired Patcher<ExemptionDto> exemptionPatcher) {

        return new PatchingHandler<>(exemptionPatcher, ExemptionDto.class, springValidatorAdapter);
    }

//    @Bean
//    PatchingHandler<TransactionDto, SpringValidatorAdapter> transactionDtoPatchingHandler(@Autowired SpringValidatorAdapter springValidatorAdapter,
//                                                                                          @Autowired Patcher<TransactionDto> transactionPatcher) {
//
//        return new PatchingHandler<>(transactionPatcher, TransactionDto.class, springValidatorAdapter);
//    }

}