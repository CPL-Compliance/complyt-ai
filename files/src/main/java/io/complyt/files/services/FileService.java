package io.complyt.files.services;

import io.complyt.files.domain.File;
import io.complyt.files.repositories.FileRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class FileService {
    @NonNull
    FileRepository fileRepository;

    public Mono<File> find() {
        return fileRepository.find();
    }
}
