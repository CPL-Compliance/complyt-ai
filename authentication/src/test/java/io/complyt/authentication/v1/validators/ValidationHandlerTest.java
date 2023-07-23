package io.complyt.authentication.v1.validators;

import io.complyt.authentication.v1.models.TokenDto;
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
    ValidationHandler<TokenDto, SpringValidatorAdapter> fileDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    @Test
    void validate_validCustomer_returnsCustomerDto() {
        TokenDto tokenDto = TestUtilities.createApiKeyDto();
        when(serverRequest.bodyToMono(TokenDto.class)).thenReturn(Mono.just(tokenDto));
        Mono<TokenDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectNext(tokenDto).verifyComplete();
    }

    @Test
    void validate_invalidCustomerDto_returnsError() {
        TokenDto customerDto = TestUtilities.createApiKeyDto().withLink("");
        when(serverRequest.bodyToMono(TokenDto.class)).thenReturn(Mono.just(customerDto));
        Mono<TokenDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectError().verify();
    }
}