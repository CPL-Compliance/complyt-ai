package io.complyt.files.v1.validators;

import TestUtils.FileDtoCreator;
import io.complyt.files.v1.models.FileDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@SpringBootTest
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<FileDto, SpringValidatorAdapter> fileDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    @Test
    void validate_validCustomer_returnsCustomerDto() {
        FileDto customerDto = FileDtoCreator.create();
        when(serverRequest.bodyToMono(FileDto.class)).thenReturn(Mono.just(customerDto));
        Mono<FileDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectNext(customerDto).verifyComplete();
    }

    @Test
    void validate_invalidCustomerDto_returnsError() {
        FileDto customerDto = FileDtoCreator.create().withLink("");
        when(serverRequest.bodyToMono(FileDto.class)).thenReturn(Mono.just(customerDto));
        Mono<FileDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectError().verify();
    }
}