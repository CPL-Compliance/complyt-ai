package io.complyt.files.services;

import io.complyt.files.domain.File;
import io.complyt.files.repositories.FileRepository;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Service
public class FileService {
    @NonNull
    FileRepository fileRepository;

    public Mono<File> find() {
        return fileRepository.find();
    }
}
