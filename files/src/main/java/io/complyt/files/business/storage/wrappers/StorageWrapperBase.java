package io.complyt.files.business.storage.wrappers;

import io.complyt.files.annotations.Generated;
import io.complyt.files.business.storage.StorageWrapper;
import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.utils.observability.ContextLogger;
import io.complyt.files.v1.exceptions.types.ObjectNotFoundApiException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
public abstract class StorageWrapperBase implements StorageWrapper {
    @Generated
    public static class StubStorageWrapper extends StorageWrapperBase {
        String hardcodedTenantId = "it_tenant"; // TODO: this should be static final and should be UPPER_CASED
        UUID hardcodedComplytId = UUID.fromString("fff38e2b-1fdd-4b43-ac7d-0058ecde600b");

        ComplytFileMetadata complytFileMetadata = new ComplytFileMetadata(
                hardcodedComplytId,
                Map.of("status", "active", "display_name", "test.it"),
                hardcodedTenantId,
                null,
                null,
                "/v1/complyt_files/fff38e2b-1fdd-4b43-ac7d-0058ecde600b");

        public StubStorageWrapper() {
            super();
        }

        @Override
        public Mono<ComplytFileMetadata> markAsDeleted(UUID complytId, String tenantId) {

            return Mono.just(complytId)
                    .filter(id -> id.equals(hardcodedComplytId))
                    .flatMap(id -> Mono.just(tenantId)
                            .filter(t -> t.equals(hardcodedTenantId))
                            .flatMap(t -> Mono.just(complytFileMetadata.withMetadata(Map.of("status", "deleted", "display_name", "test.it"))))
                    )
                    .switchIfEmpty(ContextLogger.observeCtx("ObjectNotFoundApiException thrown in StorageWrapperBase.markAsDeleted for file with complytId " + complytId + " and tenantId " + tenantId, log::error)
                            .then(Mono.error(new ObjectNotFoundApiException())));
        }

        @Override
        public Mono<ComplytFileMetadata> getSignedLinkForFile(UUID complytId, String tenantId) {
            return Mono.just(complytId)
                    .filter(id -> id.equals(hardcodedComplytId))
                    .flatMap(id -> Mono.just(tenantId)
                            .filter(t -> t.equals(hardcodedTenantId))
                            .flatMap(t -> Mono.just(complytFileMetadata.withLink(
                                    "https://storage.test.it")))
                    )
                    .switchIfEmpty(ContextLogger.observeCtx("ObjectNotFoundApiException thrown in StorageWrapperBase.getSignedLinkForFile for file with complytId " + complytId + " and tenantId " + tenantId, log::error)
                            .then(Mono.error(new ObjectNotFoundApiException())));
        }

        @Override
        public Flux<ComplytFileMetadata> listFilesInTenant(String tenantId, String files_status_query) {
            return Flux.just(complytFileMetadata)
                    .filter(fileMetadata -> fileMetadata.tenantId().equals(tenantId))
                    .filter(fileMetadata -> fileMetadata.metadata().get("status").equals(files_status_query))
                    .switchIfEmpty(Flux.empty());
        }

        @Override
        public Flux<ComplytFileMetadata> listFilesWithLinkInTenant(String tenantId, String files_status_query) {
            return Flux.just(complytFileMetadata.withLink(
                            "https://storage.test.it"))
                    .filter(fileMetadata -> fileMetadata.tenantId().equals(tenantId))
                    .filter(fileMetadata -> fileMetadata.metadata().get("status").equals(files_status_query))
                    .switchIfEmpty(Flux.empty());
        }

        @Override
        public Mono<ComplytFileMetadata> saveFile(ComplytFile complytFile) throws IOException {
            return Mono.just(complytFileMetadata);
        }
    }
}
