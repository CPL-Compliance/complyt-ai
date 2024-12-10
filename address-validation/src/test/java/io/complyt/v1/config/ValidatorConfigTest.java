package io.complyt.v1.config;

import io.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.v1.config.error_messages.StringErrorMessages;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.validators.DataConflictChecksProvider;
import io.complyt.v1.validators.ValidationHandler;
import io.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;

import java.util.Map;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ValidatorConfigTest {

    private static ValidatorConfig validatorConfig;

    @MockBean
    ServerRequest serverRequest;

    @MockBean
    SpringValidatorAdapter springValidatorAdapter;

    @MockBean
    AddressDtoQueryParamsExtractor addressDtoQueryParamsExtractor;

    @MockBean
    DataConflictChecksProvider dataConflictChecksProvider;

    @BeforeEach
    void setUp() {
        validatorConfig = new ValidatorConfig();
    }

    @Test
    void addressDtoValidationHandler_ReturnsValidationHandler() {
        // Given
        ValidationHandler<AddressDto, SpringValidatorAdapter> customerDtoValidationHandler = validatorConfig.addressDtoValidationHandler(springValidatorAdapter, addressDtoQueryParamsExtractor);
        AddressDto addressDto = TestUtilities.getAddressDto().withStreet("");
        // When
        when(serverRequest.pathVariables()).thenReturn(Map.of());
        when(addressDtoQueryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));

        Mono<AddressDto> addressDtoMono = customerDtoValidationHandler.validate(serverRequest);

        // Then
        StepVerifier.create(addressDtoMono).expectErrorMessage(
                new StringBuilder().append("Address.street ")
                        .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                        .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString());
    }

}