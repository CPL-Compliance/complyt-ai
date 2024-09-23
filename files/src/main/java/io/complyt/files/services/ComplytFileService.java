package io.complyt.files.services;

import io.complyt.files.domain.ComplytFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ComplytFileService<T> {
    Flux<T> getAllFilesByTenant(String files_status_query);

    Flux<T> getAllFilesWithLinkByTenant(String files_status_query);

    Mono<T> saveFile(ComplytFile complytFile);

    Mono<T> getSignedLinkForFile(UUID complytId);

    Mono<T> markAsDeleted(UUID complytId);
}
