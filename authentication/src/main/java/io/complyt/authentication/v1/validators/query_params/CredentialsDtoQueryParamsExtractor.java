package io.complyt.authentication.v1.validators.query_params;

import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.models.CredentialsDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class CredentialsDtoQueryParamsExtractor implements QueryParamsExtractor<CredentialsDto> {

    public Mono<CredentialsDto> extract(ServerRequest serverRequest) {
        return ContextLogger.observeCtx("CredentialsDto doesn't has request query params", log::debug)
                .then(Mono.empty());
    }
}
