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
    ValidationHandler<TokenDto, SpringValidatorAdapter> tokenDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

//    @Test
//    void validate_validToken_returnsTokenDto() {
//        TokenDto tokenDto = TestUtilities.createTokenDto();
//        when(serverRequest.bodyToMono(TokenDto.class)).thenReturn(Mono.just(tokenDto));
//        Mono<TokenDto> validationMono = tokenDtoValidationHandler.validate(serverRequest);
//
//        StepVerifier.create(validationMono).expectNext(tokenDto).verifyComplete();
//    }
//
//    @Test
//    void validate_invalidTokenDto_returnsError() {
//        TokenDto tokenDto = TestUtilities.createTokenDto().withApiKey("");
//        when(serverRequest.bodyToMono(TokenDto.class)).thenReturn(Mono.just(tokenDto));
//        Mono<TokenDto> validationMono = tokenDtoValidationHandler.validate(serverRequest);
//
//        StepVerifier.create(validationMono).expectError().verify();
//    }
}