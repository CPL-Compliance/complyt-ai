package io.complyt.files.business.storage.wrappers;

import io.complyt.files.domain.ComplytFileMetadata;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GetSignedLinkStorageWrapper {
    Mono<ComplytFileMetadata> getSignedLinkForFile(UUID complytId, String tenantId);
}
