package io.complyt.files.v1.validators;

import io.complyt.files.v1.models.FileDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import static org.mockito.Mockito.when;

@SpringBootTest
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<FileDto, SpringValidatorAdapter> fileDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub();
    }

    @Test
    void validate_validCustomer_returnsCustomerDto() {
        FileDto customerDto = objectStub.createFileDto();
        when(serverRequest.bodyToMono(FileDto.class)).thenReturn(Mono.just(customerDto));
        Mono<FileDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectNext(customerDto).verifyComplete();
    }

    @Test
    void validate_invalidCustomerDto_returnsError() {
        FileDto customerDto = objectStub.createFileDto().withLink("");
        when(serverRequest.bodyToMono(FileDto.class)).thenReturn(Mono.just(customerDto));
        Mono<FileDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectError().verify();
    }
}