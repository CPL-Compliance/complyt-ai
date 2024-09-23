package io.complyt.files.facade;

import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.services.ComplytFileServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@AllArgsConstructor
@Component
public class ComplytFileFacade {
    @NonNull
    ComplytFileServiceImpl complytFileService;

    public Flux<ComplytFileMetadata> findAllFilesInTenant(Boolean get_signed_link, String files_status_query) {

        return get_signed_link ? complytFileService.getAllFilesWithLinkByTenant(files_status_query)
                : complytFileService.getAllFilesByTenant(files_status_query);
    }

    public Mono<ComplytFileMetadata> saveFile(ComplytFile complytFile) {
        return complytFileService.saveFile(complytFile);
    }

    public Mono<ComplytFileMetadata> getSignedLinkForFile(UUID complytId) {
        return complytFileService.getSignedLinkForFile(complytId);
    }

    public Mono<ComplytFileMetadata> markAsDeleted(UUID complytId) {
        return complytFileService.markAsDeleted(complytId);
    }
}
