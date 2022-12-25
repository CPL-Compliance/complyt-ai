package io.complyt.filing.v1.handler;

import io.complyt.filing.security.permissions.FilingReadPermission;
import io.complyt.filing.services.FilingService;
import io.complyt.filing.v1.mappers.LinkMapper;
import io.complyt.filing.v1.model.LinkDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class FilingHandler {
    @NonNull
    FilingService filingService;

    @Operation(summary = "Gets link to the files")
    @ResponseStatus(HttpStatus.OK)
    @FilingReadPermission
    public Mono<ServerResponse> getOne(ServerRequest serverRequest) {
        return filingService.getOne().flatMap(
                filing -> ServerResponse.ok().body(LinkMapper.INSTANCE.linkToLinkDto(filing), LinkDto.class)
        ).switchIfEmpty(ServerResponse.notFound().build());
    }
}
