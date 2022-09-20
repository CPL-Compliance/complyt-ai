package com.complyt.v1.controllers.router.handler;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.facades.ExemptionFacade;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
public class ExemptionHandler {

    @NonNull
    private ExemptionFacade exemptionFacade;

    public Mono<ServerResponse> getOne(ServerRequest request) {
        var id = request.pathVariable("id");

        return ServerResponse.ok()
                .body(exemptionFacade.findById(id)
                        .map(ExemptionMapper.INSTANCE::exemptionToExemptionDto)
                        .switchIfEmpty(Mono.error(new NotFoundException(id))), ExemptionDto.class);
    }
    
    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(ExemptionDto.class)
                .map(ExemptionMapper.INSTANCE::exemptionDtoToExemption)
                .flatMap(exemptionFacade::save)
                .flatMap(exemption -> ServerResponse.status(HttpStatus.CREATED).bodyValue(ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption)));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        var id = request.pathVariable("id");

        return request.bodyToMono(ExemptionDto.class)
                .flatMap(exemptionDto -> {
                    Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);
                    return exemptionFacade.update(receivedExemption, id);
                })
                .flatMap(updatedExemption -> ServerResponse.status(HttpStatus.OK).bodyValue(ExemptionMapper.INSTANCE.exemptionToExemptionDto(updatedExemption)));
    }

    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .body(exemptionFacade.findAll().map(ExemptionMapper.INSTANCE::exemptionToExemptionDto), ExemptionDto.class);
    }
}
