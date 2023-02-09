package com.complyt.v1.validators;

import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.models.customer.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

@SpringBootTest()
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
    }

//    @Test
//    void validate_validCustomer_returnsCustomerDto() {
//        CustomerDto customerDto = objectStub.createCustomerDto(UUID.randomUUID().toString());
//        when(serverRequest.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customerDto));
//        Mono<CustomerDto> validationMono = customerDtoValidationHandler.validate(serverRequest);
//
//        StepVerifier.create(validationMono).expectNext(customerDto).verifyComplete();
//    }
//
//    @Test
//    void validate_invalidCustomerDto_returnsError() {
//        CustomerDto customerDto = objectStub.createCustomerDto(UUID.randomUUID().toString()).withName("");
//        when(serverRequest.bodyToMono(CustomerDto.class)).thenReturn(Mono.just(customerDto));
//        Mono<CustomerDto> validationMono = customerDtoValidationHandler.validate(serverRequest);
//
//        StepVerifier.create(validationMono).expectError().verify();
//    }
}