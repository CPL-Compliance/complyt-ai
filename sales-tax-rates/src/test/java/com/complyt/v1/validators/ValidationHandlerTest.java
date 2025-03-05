package com.complyt.v1.validators;

import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {ValidatorConfig.class})
@SpringBootTest()
class ValidationHandlerTest {

    @MockBean
    SpringValidatorAdapter springValidatorAdapter;

    @MockBean
    DataConflictChecksProvider dataConflictChecksProvider;

    @MockBean
    AddressDtoQueryParamsExtractor queryParamsExtractor;

    @MockBean
    ParameterChecksProvider paramChecksProvider;

    @MockBean
    ShouldCallValidate shouldCallValidate;

    @Autowired
    ValidationHandler<AddressWithDateDto, SpringValidatorAdapter> addressDtoValidationHandler;


    @MockBean
    ServerRequest serverRequest;

    BiFunction<AddressWithDateDto, ServerRequest, Mono<String>> CHECK = (address, serverRequest) -> Mono.just("error");
    private final AddressWithDateDto addressDto = TestUtilities.createAddressWithDateDtoInCalifornia("2000-01-01");

    // todo return
    @Test
    void validate_ValidAndUnconflictedDto_ReturnsDto() {
        Map<String, String> pathVariables = new HashMap<>();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/transactions/source/someSource/externalId/someExternalId");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<AddressWithDateDto> result = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void validate_Error_AddressDto_ReturnsError() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        // When
        when(serverRequest.bodyToMono(AddressWithDateDto.class)).thenReturn(Mono.just(addressDto));
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(serverRequest.queryParams()).thenReturn(map);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(CHECK));

        Mono<AddressWithDateDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError().verify();
    }
}
