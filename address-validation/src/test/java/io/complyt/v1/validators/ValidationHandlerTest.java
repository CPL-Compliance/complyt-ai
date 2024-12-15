package io.complyt.v1.validators;

import io.complyt.utils.exceptions.types.ObjectNotValidException;
import io.complyt.v1.config.BodyCheckConfig;
import io.complyt.v1.config.ValidatorConfig;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.validators.address_body_checks.AddressExistingAbbreviationBodyCheck;
import io.complyt.v1.validators.address_body_checks.PartialAddressBodyChecker;
import io.complyt.v1.validators.query_params.QueryParamsExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.util.List;
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
    QueryParamsExtractor<AddressDto> queryParamsExtractor;

    @Autowired
    ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    BiFunction<AddressDto, ServerRequest, Mono<Boolean>> CHECK = (address, serverRequest) -> Mono.just(true);

    BodyCheckConfig<AddressDto> bodyCheckConfig;

    MultiValueMap<String, String> queryParamsMap = new LinkedMultiValueMap<>();

    @BeforeEach
    void setup() {
        bodyCheckConfig =  new BodyCheckConfig<AddressDto>(List.of(
                new PartialAddressBodyChecker(),
                new AddressExistingAbbreviationBodyCheck()));

        queryParamsMap.add("key", "value");
    }

    @Test
    void validate_ValidAndUnconflictedDto_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.getAddressDto();
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("key", "value");

        // When
        when(serverRequest.queryParams()).thenReturn(queryParamsMap);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(bodyCheckConfig.entityDtoFluxFunction()));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(addressDto).verifyComplete();
    }

    @Test
    void validate_ValidAndConflictedDtoBecauseOfNullCity_ReturnsNotValidException() {
        // Given
        AddressDto addressDto = TestUtilities.getAddressDto().withCity(null);

        // When
        when(serverRequest.queryParams()).thenReturn(queryParamsMap);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(bodyCheckConfig.entityDtoFluxFunction()));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidException.class).verify();
    }

    @Test
    void validate_ValidAndConflictedDtoBecauseOfNullStreet_ReturnsNotValidException() {
        // Given
        AddressDto addressDto = TestUtilities.getAddressDto().withStreet(null);

        // When
        when(serverRequest.queryParams()).thenReturn(queryParamsMap);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(bodyCheckConfig.entityDtoFluxFunction()));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ObjectNotValidException.class).verify();
    }

    @Test
    void validate_PartialAddressValidAndUnconflictedDto_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.getAddressDto().withPartial(true);

        // When
        when(serverRequest.queryParams()).thenReturn(queryParamsMap);
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(bodyCheckConfig.entityDtoFluxFunction()));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(addressDto).verifyComplete();
    }
}