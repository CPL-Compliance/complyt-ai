package com.example.complyt.v1.validators;

import com.complyt.v1.config.BodyCheckConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.exceptions.types.PathVariableErrorException;
import com.complyt.v1.exceptions.types.QueryParamErrorException;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.validators.DataConflictChecksProvider;
import com.complyt.v1.validators.ParameterChecksProvider;
import com.complyt.v1.validators.ShouldCallValidate;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
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
import java.util.function.Function;

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

    @MockBean
    ShouldCallValidate shouldCallValidate;

    @MockBean
    ParameterChecksProvider paramChecksProvider;

    BiFunction<AddressDto, ServerRequest, Mono<Boolean>> CHECK = (address, serverRequest) -> Mono.just(true);
    Mono<Function<String, Mono<String>>> CHECK_INVALID = Mono.just((String param) -> Mono.just("error"));


    @Test
    public void handle_PathVariableNotValid_ReturnsPathVariableError() {
        // Given
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("param", "invalidParam");
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.pathVariable("param")).thenReturn("invalidParam");
        when(paramChecksProvider.getFunctionCheck("param")).thenReturn((CHECK_INVALID));

        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(paramChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(false));
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/notFound");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<AddressDto> result = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectError(PathVariableErrorException.class)
                .verify();
    }

    @Test
    public void handle_QueryParamNotValid_ReturnsQueryParamError() {
        // Given
        Map<String, String> pathVariables = new HashMap<>();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("param", "invalidParam");


        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.pathVariable("param")).thenReturn("invalidParam");
        when(paramChecksProvider.getFunctionCheck("param")).thenReturn((CHECK_INVALID));


        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(paramChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(true));
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/notFound");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<AddressDto> result = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectError(QueryParamErrorException.class)
                .verify();
    }

    @Test
    public void handle_NoParamsValid_ReturnsMonoEmpty() {
        // Given
        Map<String, String> pathVariables = new HashMap<>();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.pathVariables()).thenReturn(pathVariables);
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(paramChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.just(false));
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/notFound");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        Mono<AddressDto> result = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    public void handle_WithValidQueryParams_ReturnsMonoEmpty() {
        // Given
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("state", "CA");
        queryParams.add("zip", "12345");
        Map<String, String> pathVariables = new HashMap<>();
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();


        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(paramChecksProvider.doesParamExist(serverRequest)).thenReturn(Mono.empty());
        when(paramChecksProvider.getFunctionCheck("state")).thenReturn(Mono.empty());
        when(paramChecksProvider.getFunctionCheck("zip")).thenReturn(Mono.empty());

        when(serverRequest.pathVariables()).thenReturn(pathVariables);

        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(shouldCallValidate.apply(serverRequest)).thenReturn(false);

        when(serverRequest.path()).thenReturn("/v1/notFound");
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));

        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> result = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }


    @Test
    void handle_ValidAndUnconflictedDto_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/sales_tax_rates");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(addressDto).verifyComplete();
    }

    @Test
    void handle_ValidAndconflictedDtoBecauseOfNullCity_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withCity(null);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/sales_tax_rates");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void handle_ValidAndConflictedDtoBecauseOfNullCountry_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withCountry(null);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/sales_tax_rates");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void handle_ValidAndconflictedDtoBecauseOfNullStreet_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withStreet(null);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/sales_tax_rates");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void handle_PartialAddressValidAndUnconflictedDto_ReturnsDto() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia().withPartial(true);
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        // When
        when(serverRequest.queryParams()).thenReturn(queryParams);
        when(serverRequest.method()).thenReturn(HttpMethod.GET);
        when(serverRequest.path()).thenReturn("/v1/sales_tax_rates");
        when(shouldCallValidate.apply(serverRequest)).thenReturn(true);
        when(serverRequest.pathVariables()).thenReturn(Map.of("something", "something"));
        when(queryParamsExtractor.extract(serverRequest)).thenReturn(Mono.just(addressDto));
        when(dataConflictChecksProvider.getPathVariableCheck("something")).thenReturn(Mono.just(CHECK));
        when(dataConflictChecksProvider.getBodyConflictCheck()).thenReturn(Mono.just(BodyCheckConfig.ADDRESS_BODY_CHECK));

        Mono<AddressDto> validationMono = addressDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(validationMono).expectNext(addressDto).verifyComplete();
    }
}
