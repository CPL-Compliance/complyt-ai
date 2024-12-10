package com.complyt.v1.validators;

import com.complyt.v1.config.BodyCheckConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.validators.body_checkers.AddressChecker;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
import kotlin.Function;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

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
    QueryParamsExtractor<AddressWithDateDto> queryParamsExtractor;

    @Autowired
    ValidationHandler<AddressWithDateDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    BiFunction<AddressWithDateDto, ServerRequest, Mono<String>> CHECK = (address, serverRequest) -> Mono.just("error");
    private final AddressWithDateDto addressDto = TestUtilities.createAddressWithDateDtoInCalifornia("2000-01-01");

    @Test
    void validate_ValidAndUnconflictedDto_ReturnsDto() {
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
        StepVerifier.create(validationMono).expectNext(addressDto).verifyComplete();
    }
}
