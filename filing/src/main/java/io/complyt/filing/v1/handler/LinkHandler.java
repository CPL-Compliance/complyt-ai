package io.complyt.filing.v1.handler;

import io.complyt.filing.security.permissions.LinkReadPermission;
import io.complyt.filing.services.LinkService;
import io.complyt.filing.v1.mappers.LinkMapper;
import io.complyt.filing.v1.model.LinkDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Link", description = "This is the Links controller")
public class LinkHandler {
    @NonNull
    LinkService linkService;

    @Operation(summary = "Gets link to the files")
    @ResponseStatus(HttpStatus.OK)
    @LinkReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        return ServerResponse
                .ok()
                .body(linkService.find().map(LinkMapper.INSTANCE::linkToLinkDto), LinkDto.class);
    }
}
