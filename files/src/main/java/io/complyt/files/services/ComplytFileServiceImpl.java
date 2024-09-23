package io.complyt.files.services;

import io.complyt.files.business.storage.StorageWrapper;
import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.security.TenantResolver;
import io.complyt.files.v1.exceptions.types.ComplytApiException;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ComplytFileServiceImpl<T> implements ComplytFileService {
    @NonNull
    StorageWrapper storageWrapper;

    @NonNull
    TenantResolver tenantResolver;

    @Override
    public Flux<ComplytFileMetadata> getAllFilesByTenant(String files_status_query) {
        return tenantResolver.resolve().flatMapMany(tenantId -> storageWrapper.listFilesInTenant(tenantId, files_status_query));
    }

    @Override
    public Flux<ComplytFileMetadata> getAllFilesWithLinkByTenant(String files_status_query) {
        return tenantResolver.resolve().flatMapMany(tenantId -> storageWrapper.listFilesWithLinkInTenant(tenantId, files_status_query));
    }

    @Override
    public Mono<ComplytFileMetadata> saveFile(ComplytFile complytFile) {
        return tenantResolver.resolve().flatMap(tenantId -> {
            try {
                return storageWrapper.saveFile(complytFile.withMetadata(complytFile.getMetadata().withTenantId(tenantId)));
            } catch (IOException e) {
                throw new ComplytApiException(e.toString());
            }
        });
    }

    @Override
    public Mono<ComplytFileMetadata> getSignedLinkForFile(UUID complytId) {
        return tenantResolver.resolve().flatMap(tenantId -> storageWrapper.getSignedLinkForFile(complytId, tenantId));
    }

    @Override
    public Mono<ComplytFileMetadata> markAsDeleted(UUID complytId) {
        return tenantResolver.resolve().flatMap(tenantId -> storageWrapper.markAsDeleted(complytId, tenantId));
    }
}
