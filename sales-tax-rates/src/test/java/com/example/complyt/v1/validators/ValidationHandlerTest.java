package com.example.complyt.v1.validators;

import com.complyt.v1.config.BodyCheckConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
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
    QueryParamsExtractor<AddressDto> queryParamsExtractor;

    @Autowired
    ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @MockBean
    ServerRequest serverRequest;

    BiFunction<AddressDto, ServerRequest, Mono<Boolean>> CHECK = (address, serverRequest) -> Mono.just(true);

    @Test
    void validate_ValidAndUnconflictedDto_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();

        // When
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(addressDto).verifyComplete();
    }

    @Test
    void validate_ValidAndconflictedDtoBecauseOfNullCity_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withCity(null);

        // When
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void validate_ValidAndConflictedDtoBecauseOfNullCountry_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withCountry(null);

        // When
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void validate_ValidAndconflictedDtoBecauseOfNullStreet_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withStreet(null);

        // When
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void validate_PartialAddressValidAndUnconflictedDto_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withPartial(true);

        // When
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(addressDto).verifyComplete();
    }
}
