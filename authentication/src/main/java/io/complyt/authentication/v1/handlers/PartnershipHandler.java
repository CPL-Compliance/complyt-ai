package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.PartnershipFacade;
import io.complyt.authentication.security.permissions.partner.PartnerPermission;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.exceptions.types.PartnerNotFoundApiException;
import io.complyt.authentication.v1.exceptions.types.SpecificReferralNotFoundApiException;
import io.complyt.authentication.v1.mappers.PartnershipMapper;
import io.complyt.authentication.v1.mappers.ReferralMapper;
import io.complyt.authentication.v1.models.PartnershipDto;
import io.complyt.authentication.v1.models.ReferralDto;
import io.complyt.authentication.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartnershipHandler {
    @NonNull
    PartnershipFacade partnershipFacade;

    @NonNull
    ValidationHandler<PartnershipDto, SpringValidatorAdapter> partnershipValidationHandler;

    @NonNull
    ValidationHandler<ReferralDto, SpringValidatorAdapter> referralValidationHandler;

    @PartnerPermission
    public Mono<ServerResponse> getPartnership(@NonNull ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<PartnershipDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(partnershipValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(partnershipFacade::getPartnership)
                        .map(PartnershipMapper.INSTANCE::partnershipToPartnershipDto)
                        .flatMap(partnershipDto -> ContextLogger.observeCtx("<-- Returned Body: Token", log::info).thenReturn(partnershipDto))
                        .switchIfEmpty(ContextLogger.observeCtx("Failed to get Partnership", log::error)
                                .then(Mono.error(new PartnerNotFoundApiException()))));

        return ServerResponse.ok().body(value, PartnershipDto.class);
    }

    @PartnerPermission
    public Mono<ServerResponse> upsertReferral(@NonNull ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<PartnershipDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(referralValidationHandler.handle(serverRequest))
                .map(ReferralMapper.INSTANCE::referralDtoToReferral)
                .flatMap(partnershipFacade::upsertReferralClient)
                .map(PartnershipMapper.INSTANCE::partnershipToPartnershipDto)
                .flatMap(partnershipDto -> ContextLogger.observeCtx("<-- Returned Body: Token", log::info).thenReturn(partnershipDto))
                .switchIfEmpty(ContextLogger.observeCtx("Failed to get Partnership", log::error)
                        .then(Mono.error(new PartnerNotFoundApiException())));

        return ServerResponse.ok().body(value, PartnershipDto.class);
    }

    @PartnerPermission
    public Mono<ServerResponse> deleteReferral(@NonNull ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());
        String requestedTenantId = serverRequest.queryParam("tenantId")
                .orElse("Invalid tenantId");

        Mono<PartnershipDto> value = ContextLogger.observeCtx(logStr, log::info)
                .then(partnershipValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> partnershipFacade.markReferralAsCancelled(requestedTenantId)
                        .map(PartnershipMapper.INSTANCE::partnershipToPartnershipDto)
                        .flatMap(partnershipDto -> ContextLogger.observeCtx("<-- Returned Body: Token", log::info).thenReturn(partnershipDto))
                        .switchIfEmpty(ContextLogger.observeCtx("Failed to get Partnership", log::error)
                                .then(Mono.error(new SpecificReferralNotFoundApiException())))));

        return ServerResponse.ok().body(value, PartnershipDto.class);
    }
}
