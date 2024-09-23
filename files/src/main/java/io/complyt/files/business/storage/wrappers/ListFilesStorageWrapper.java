package io.complyt.files.business.storage.wrappers;

import io.complyt.files.domain.ComplytFileMetadata;
import reactor.core.publisher.Flux;

public interface ListFilesStorageWrapper {
    Flux<ComplytFileMetadata> listFilesInTenant(String tenantId, String files_status_query);

    Flux<ComplytFileMetadata> listFilesWithLinkInTenant(String tenantId, String files_status_query);
}