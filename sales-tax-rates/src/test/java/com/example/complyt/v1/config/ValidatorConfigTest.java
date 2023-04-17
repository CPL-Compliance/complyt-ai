//package com.example.complyt.v1.config;
//
//import com.complyt.v1.config.ValidatorConfig;
//import com.complyt.v1.model.AddressDto;
//import com.complyt.v1.validators.ValidationHandler;
//import com.testUtils.TestUtilities;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.mockito.Mockito.when;
//
//public class ValidatorConfigTest {
//
//    private static ValidatorConfig validatorConfig;
//
//    @MockBean
//    ServerRequest serverRequest;
//
//    @MockBean
//    SpringValidatorAdapter springValidatorAdapter;
//
//    @Test
//    void salesTaxTrackingDtoValidationHandler_ReturnsValidationHandler() {
//        // Given
//        ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler = validatorConfig.addressDtoValidationHandler(springValidatorAdapter);
//        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();
//
//        // When
//
//        Mono<AddressDto> salesTaxTrackingDtoMono = addressDtoValidationHandler.validate(addressDto);
//
//        // Then
//        StepVerifier.create(salesTaxTrackingDtoMono).expectErrorMessage(GenericErrorMessages.DATA_CONFLICT_ERROR);
//    }
//}
