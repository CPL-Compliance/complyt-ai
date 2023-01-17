package com.complyt.v1.validators;

import com.complyt.v1.models.customer.CustomerDto;
import org.hibernate.validator.internal.engine.ValidatorFactoryImpl;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.support.ServerRequestWrapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.CustomerDtoCreator;

import static org.mockito.Mockito.when;

@SpringBootTest()
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    @Test
    void validate_validCustomer_returnsCustomerDto() {
        CustomerDto customerDto = CustomerDtoCreator.create();
        when(serverRequest.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customerDto));
        Mono<CustomerDto> validationMono = customerDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectNext(customerDto).verifyComplete();
    }

    @Test
    void validate_invalidCustomerDto_returnsError() {
        CustomerDto customerDto = CustomerDtoCreator.create().withName("");
        when(serverRequest.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customerDto));
        Mono<CustomerDto> validationMono = customerDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectError().verify();
    }
}