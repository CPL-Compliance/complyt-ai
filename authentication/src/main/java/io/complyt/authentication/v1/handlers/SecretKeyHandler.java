package io.complyt.authentication.v1.handlers;


import io.complyt.authentication.security.permissions.secret_key.SecretKeyCreatePermission;
import io.complyt.authentication.services.AesSecretKeyService;
import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.models.SecretKeyDto;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecretKeyHandler {

    @NonNull
    AesSecretKeyService aesSecretKeyService;

    @SecretKeyCreatePermission
    public Mono<ServerResponse> get(@NonNull ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(),
                serverRequest.path());

        Mono<SecretKeyDto> value = ContextLogger.observeCtx(logStr, log::info)
                .thenReturn(Objects.requireNonNull(aesSecretKeyService.generate256AesKey()))
                .map(aesSecretKeyService::convertSecretKeyToString)
                .map(SecretKeyDto::new);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(value, SecretKeyDto.class);
    }
}
