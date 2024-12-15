package io.complyt.v1.routers;

import io.complyt.domain.Address;
import io.complyt.facades.ValidAddressFacade;
import io.complyt.v1.config.ApiExceptionConfig;
import io.complyt.v1.config.ValidatorConfig;
import io.complyt.v1.config.error_messages.DtoErrorMessages;
import io.complyt.v1.config.error_messages.GenericErrorMessages;
import io.complyt.v1.config.error_messages.StringErrorMessages;
import io.complyt.v1.handlers.ValidAddressHandler;
import io.complyt.v1.handlers.exceptions.GlobalErrorAttributes;
import io.complyt.v1.handlers.exceptions.GlobalExceptionHandler;
import io.complyt.v1.mappers.AddressMapper;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.validators.query_params.AddressDtoQueryParamsExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test_utils.TestUtilities;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@WebFluxTest
@ContextConfiguration(classes = {AddressRouter.class, ValidAddressHandler.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class,
        AddressDtoQueryParamsExtractor.class,
        ApiExceptionConfig.class})
class ValidAddressHandlerTest implements ValidAddressHandlerTestTemplate {

    @Autowired
    AddressRouter addressRouter;
    @Autowired
    WebTestClient webTestClient;
    AddressDto addressDto = TestUtilities.getAddressDto();
    @MockBean
    private ValidAddressFacade validAddressFacade;

    @Override
    @WithMockUser
    @Test
    public void getAny_InvalidUrl_Returns404() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/resource_not_found").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Override
    @WithMockUser
    @Test
    public void get_Exists_Returns200() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);
        AddressDto expectedAddressDto = AddressMapper.INSTANCE.addressToAddressDto(address);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.just(address));

        // Then
        webTestClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(100)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressDto.class)
                .value(addressWithSalesTaxRatesItem -> addressWithSalesTaxRatesItem, equalTo(expectedAddressDto));
    }

    @Override
    @Test
    public void get_UnauthenticatedUser_Returns401() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(AddressRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void get_NotFound_Returns400() {
        // Given
        Address givenAddress = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(givenAddress)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.ADDRESS_NOT_VALID, map.get("message")));
    }

    @Override
    @WithMockUser
    @Test
    public void get_InternalServerError_Returns500() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @WithMockUser
    @Test
    public void get_NullHandler_ThrowsNullPointerException() {
        // Given
        ValidAddressHandler nullValidAddressHandler = null;
        AddressRouter addressRouter = new AddressRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            addressRouter.GetValidAddressByAddress(nullValidAddressHandler);
        });

        // Then
        assertEquals("validAddressHandler " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE, exception.getMessage());
    }

    @Override
    @WithMockUser
    @Test
    public void get_NullCity_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of(new StringBuilder().append("Address.city ")
                                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString())));
    }

    @Override
    @WithMockUser
    @Test
    public void get_NullCountry_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of(new StringBuilder().append("Address.country ")
                                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).toString())));
    }

    @Override
    @WithMockUser
    @Test
    public void get_NullState_Returns400FailedValidation() {

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_NullStreet_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of(new StringBuilder().append("Address.street ")
                                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString())));
    }

    @Override
    @WithMockUser
    @Test
    public void get_NullZip_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.zip " + StringErrorMessages.NOT_BE_BLANK_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_BlankZip_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", "")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.zip " + StringErrorMessages.NOT_BE_BLANK_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_BlankCountry_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", "")
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of(new StringBuilder().append("Address.country ")
                                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).toString())));
    }

    @Override
    @WithMockUser
    @Test
    public void get_BlankStreet_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", "")
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of(new StringBuilder().append("Address.street ")
                                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString())));
    }

    @Override
    @WithMockUser
    @Test
    public void get_BlankCity_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", "")
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of(new StringBuilder().append("Address.city ")
                                .append(StringErrorMessages.NOT_BE_BLANK_ERROR).append(" ")
                                .append(DtoErrorMessages.NON_PARTIAL_ERROR_SUFFIX).toString())));
    }

    @Override
    @WithMockUser
    @Test
    public void get_BlankState_Returns400FailedValidation() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", "")
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.state " + StringErrorMessages.NOT_BE_BLANK_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_ZipMoreThan20Char_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", TestUtilities.stringByLength(21))
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.zip " + StringErrorMessages.MAX_20_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_CountryMoreThan50Char_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", TestUtilities.stringByLength(51))
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.country " + StringErrorMessages.MAX_50_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_StreetMoreThan200Char_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", TestUtilities.stringByLength(201))
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.street " + StringErrorMessages.MAX_200_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_StateMoreThan100Char_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .mutate()
                .responseTimeout(Duration.ofSeconds(50)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", TestUtilities.stringByLength(101))
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.state " + StringErrorMessages.MAX_100_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_CityMoreThan100Char_Returns400FailedValidation() {
        // Given
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(validAddressFacade.validateAddress(address)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("country", addressDto.country())
                        .queryParam("state", addressDto.state())
                        .queryParam("city", TestUtilities.stringByLength(101))
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> TestUtilities.checkErrorMessages(map,
                        Set.of("Address.city " + StringErrorMessages.MAX_100_ERROR)));
    }

    @Override
    @WithMockUser
    @Test
    public void get_partialAddressWithMinimumParams_Returns200() {
        // Given
        AddressDto givenAddressDto = new AddressDto(null, addressDto.country(), null, addressDto.state(), null, addressDto.zip(), true);
        Address mappedAddress = AddressMapper.INSTANCE.addressDtoToAddress(givenAddressDto);
        AddressDto expectedAddressDto = AddressMapper.INSTANCE.addressToAddressDto(mappedAddress);

        // When
        when(validAddressFacade.validateAddress(mappedAddress)).thenReturn(Mono.just(mappedAddress));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("state", givenAddressDto.state())
                        .queryParam("zip", givenAddressDto.zip())
                        .queryParam("country", givenAddressDto.country())
                        .queryParam("isPartial", true)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressDto.class)
                .isEqualTo(expectedAddressDto);
    }
}