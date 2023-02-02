package com.complyt.v1.handlers;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.facades.ExemptionFacade;
import com.complyt.security.permissions.exemption.ExemptionCreatePermission;
import com.complyt.security.permissions.exemption.ExemptionDeletePermission;
import com.complyt.security.permissions.exemption.ExemptionReadPermission;
import com.complyt.security.permissions.exemption.ExemptionUpdatePermission;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExemptionHandler {

    @NonNull
    private ExemptionFacade exemptionFacade;

    @NonNull
    ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler;

    @ExemptionReadPermission
    public Mono<ServerResponse> findByComplytId(ServerRequest request) {
        UUID complytId = UUID.fromString(request.pathVariable("complytId"));

        return ServerResponse.ok()
                .body(exemptionFacade.findByComplytId(complytId)
                        .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exemption with complyt id " + complytId + " not found"))), ExemptionDto.class);
    }

    @ExemptionCreatePermission
    public Mono<ServerResponse> create(ServerRequest request) {

        return exemptionDtoValidationHandler.validate(request)
                .map(ExemptionMapper.INSTANCE::exemptionDtoToExemption)
                .flatMap(exemptionFacade::save).log()
                .flatMap(exemption -> ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption)));
    }

    @ExemptionUpdatePermission
    public Mono<ServerResponse> update(ServerRequest request) {
        UUID complytId = UUID.fromString(request.pathVariable("complytId"));

        return exemptionDtoValidationHandler.validate(request)
                .flatMap(exemptionDto -> {
                    Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);
                    return exemptionFacade.update(receivedExemption, complytId);
                })
                .flatMap(updatedExemption -> ServerResponse.status(HttpStatus.OK).bodyValue(ExemptionMapper.INSTANCE.exemptionToExemptionDto(updatedExemption)))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException("Exemption not found by complytId " + complytId)));
    }

    @ExemptionReadPermission
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(exemptionFacade.findAll().map(ExemptionMapper.INSTANCE::exemptionToExemptionDto), ExemptionDto.class);
    }

    @ExemptionDeletePermission
    public Mono<ServerResponse> delete(ServerRequest request) {
        UUID complytId = UUID.fromString(request.pathVariable("complytId"));

        return exemptionFacade.delete(complytId)
                .flatMap(deleteResult -> ServerResponse.noContent().build())
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException("Exemption with complyt id " + complytId + " was not found")));
    }
}
