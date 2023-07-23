package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.ApiKeyDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import static org.mockito.Mockito.when;

@SpringBootTest
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<ApiKeyDto, SpringValidatorAdapter> fileDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    @Test
    void validate_validCustomer_returnsCustomerDto() {
        ApiKeyDto apiKeyDto = TestUtilities.createApiKeyDto();
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(apiKeyDto));
        Mono<ApiKeyDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectNext(apiKeyDto).verifyComplete();
    }

    @Test
    void validate_invalidCustomerDto_returnsError() {
        ApiKeyDto customerDto = TestUtilities.createApiKeyDto().withLink("");
        when(serverRequest.bodyToMono(ApiKeyDto.class)).thenReturn(Mono.just(customerDto));
        Mono<ApiKeyDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectError().verify();
    }
}