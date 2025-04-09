package com.complyt.v1.handlers;


import com.complyt.facades.VatValidationFacade;
import com.complyt.security.permissions.vat_validation.VatValidationReadPermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ComplytApiException;
import com.complyt.v1.mappers.ValidatedVatMapper;
import com.complyt.v1.mappers.VatDetailsToValidateMapper;
import com.complyt.v1.models.vat_validation.ValidatedVatDto;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import com.complyt.v1.routers.VatValidationRouter;
import com.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VatValidationHandler {
    @NonNull
    ValidationHandler<VatDetailsToValidateDto, SpringValidatorAdapter> validatedVatDtoValidationHandler;

    @NonNull
    VatValidationFacade vatValidationFacade;

    @VatValidationReadPermission
    public Mono<ServerResponse> validatedVat(ServerRequest serverRequest) {
        String resourceURI = VatValidationRouter.BASE_URL + "/validate";

        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        return ContextLogger.observeCtx(logStr, log::info)
                .then(validatedVatDtoValidationHandler.handle(serverRequest)
                        .flatMap(validatedVatDto -> ContextLogger.observeCtx("--> Body: " + validatedVatDto, log::info)
                                .thenReturn(validatedVatDto))
                        .map(VatDetailsToValidateMapper.INSTANCE::vatDetailsToValidateDtoToVatDetailsToValidate)
                        .flatMap(vatDetailsToValidate -> vatValidationFacade.findValidatedVat(vatDetailsToValidate)
                                .map(ValidatedVatMapper.INSTANCE::validatedVatToValidatedVatDto)
                                .flatMap(foundValidatedVatDto -> ContextLogger.observeCtx("<-- Returned Body: " + foundValidatedVatDto, log::info)
                                        .thenReturn(foundValidatedVatDto))
                                .flatMap(foundVatDto -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                                        .body(Mono.just(foundVatDto), ValidatedVatDto.class))
                                .switchIfEmpty(vatValidationFacade.validateVat(vatDetailsToValidate)
                                        .map(ValidatedVatMapper.INSTANCE::validatedVatToValidatedVatDto)
                                        .map(newValidatedVatDto -> ContextLogger.observeCtx("<-- Returned Body: " + newValidatedVatDto, log::info)
                                                .thenReturn(newValidatedVatDto))
                                        .flatMap(newValidatedVatDto -> ServerResponse.created(URI.create(resourceURI)).contentType(MediaType.APPLICATION_JSON)
                                                .body(newValidatedVatDto, ValidatedVatDto.class))
                                        .switchIfEmpty(ContextLogger.observeCtx("Failed to validate vat due to flow error: vat country code:" + vatDetailsToValidate.getVatNumber() + ", vat number: " + vatDetailsToValidate.getVatNumber(), log::error) //todo: insert vat details here - change location of the mono objects
                                                .then(Mono.error(new ComplytApiException("Could not validated vat")))))));
    }
}
