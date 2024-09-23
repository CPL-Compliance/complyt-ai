package io.complyt.files.business.storage.wrappers;

import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import reactor.core.publisher.Mono;

import java.io.IOException;

public interface SaveFileStorageWrapper {
    Mono<ComplytFileMetadata> saveFile(ComplytFile complytFile) throws IOException;
}
