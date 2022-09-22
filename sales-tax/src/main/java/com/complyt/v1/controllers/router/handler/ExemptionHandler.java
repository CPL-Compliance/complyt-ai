package com.complyt.v1.controllers.router.handler;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.facades.ExemptionFacade;
import com.complyt.security.permissions.exemption.ExemptionCreatePermission;
import com.complyt.security.permissions.exemption.ExemptionReadPermission;
import com.complyt.security.permissions.exemption.ExemptionUpdatePermission;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
public class ExemptionHandler {

    @NonNull
    private ExemptionFacade exemptionFacade;

    @Operation(summary = "Gets exemption by id")
    @ResponseStatus(HttpStatus.OK)
    @ExemptionReadPermission
    public Mono<ServerResponse> getOne(ServerRequest request) {
        String id = request.pathVariable("id");

        return ServerResponse.ok()
                .body(exemptionFacade.findById(id)
                        .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exemption with id " + id + "not found"))), ExemptionDto.class);
    }

    @Operation(summary = "This will update the exemption if found by id, otherwise it will create it")
    @ResponseStatus(HttpStatus.OK)
    @ExemptionCreatePermission
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ExemptionDto.class)
                .map(ExemptionMapper.INSTANCE::exemptionDtoToExemption).log()
                .flatMap(exemptionFacade::save).log()
                .flatMap(exemption -> ServerResponse.status(HttpStatus.CREATED)
                        .bodyValue(ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption)));
    }

    @Operation(summary = "This will update the exemption if found by id, otherwise it will throw an error")
    @ResponseStatus(HttpStatus.OK)
    @ExemptionUpdatePermission
    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");

        return request.bodyToMono(ExemptionDto.class)
                .flatMap(exemptionDto -> {
                    Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);
                    return exemptionFacade.update(receivedExemption, id);
                })
                .flatMap(updatedExemption -> ServerResponse.status(HttpStatus.OK).bodyValue(ExemptionMapper.INSTANCE.exemptionToExemptionDto(updatedExemption)))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exemption not found")));
    }

    @Operation(summary = "Gets all exemptions")
    @ResponseStatus(HttpStatus.OK)
    @ExemptionReadPermission
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(exemptionFacade.findAll().map(ExemptionMapper.INSTANCE::exemptionToExemptionDto), ExemptionDto.class);
    }

    @Operation(summary = "This will delete the exemption if found by id")
    @ResponseStatus(HttpStatus.OK)
    @ExemptionCreatePermission
    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");

        return exemptionFacade.delete(id)
                .flatMap(deleteResult -> ServerResponse.noContent().build())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Exemption with id " + id + " was not found")));
    }
}
