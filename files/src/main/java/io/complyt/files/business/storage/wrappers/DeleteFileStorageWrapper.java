package io.complyt.files.business.storage.wrappers;

import io.complyt.files.domain.ComplytFileMetadata;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DeleteFileStorageWrapper {
    Mono<ComplytFileMetadata> markAsDeleted(UUID complytId, String tenantId);
}
