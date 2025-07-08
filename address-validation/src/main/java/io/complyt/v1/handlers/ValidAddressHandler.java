package io.complyt.v1.handlers;

import io.complyt.domain.CachedAddressData;
import io.complyt.facades.ValidAddressFacade;
import io.complyt.security.permissions.address_validation.AddressValidationReadPermission;
import io.complyt.utils.exceptions.types.ObjectNotValidException;
import io.complyt.utils.observability.ContextLogger;
import io.complyt.v1.config.error_messages.GenericErrorMessages;
import io.complyt.v1.mappers.AddressMapper;
import io.complyt.v1.mappers.CachedAddressDataMapper;
import io.complyt.v1.mappers.ValidateAddressMapper;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.models.CachedAddressDataDto;
import io.complyt.v1.models.ValidatedAddressDto;
import io.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ValidAddressHandler {

    @NonNull
    ValidAddressFacade validAddressFacade;

    @NonNull
    ValidationHandler<AddressDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @AddressValidationReadPermission
    public Mono<ServerResponse> getValidAddressByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ValidatedAddressDto> validatedAddressDto = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoValidationHandler.validate(serverRequest))
                .map(AddressMapper.INSTANCE::addressDtoToAddress)
                .flatMap(validAddressFacade::validateAddress)
                .map(ValidateAddressMapper.INSTANCE::validatedAddressToValidatedAddressDto)
                .switchIfEmpty(Mono.error(new ObjectNotValidException(GenericErrorMessages.ADDRESS_NOT_VALID)));
        
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(validatedAddressDto, ValidatedAddressDto.class);
    }

    @AddressValidationReadPermission
    public Mono<ServerResponse> resolveValidAddressByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<CachedAddressDataDto> cachedAddressDataDto = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoValidationHandler.validate(serverRequest))
                .map(AddressMapper.INSTANCE::addressDtoToAddress)
                .flatMap(validAddressFacade::resolveAddress)
                .map(CachedAddressDataMapper.INSTANCE::cachedAddressDataToCachedAddressDataDto)
                .switchIfEmpty(Mono.error(new ObjectNotValidException(GenericErrorMessages.ADDRESS_NOT_VALID)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(cachedAddressDataDto, CachedAddressData.class);
    }
}