package io.complyt.files.v1.validators.query_params;

import io.complyt.files.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class QueryParamsExtractorFile implements QueryParamsExtractor<ComplytFileDto> {

@Override
public Mono<ComplytFileDto> extract(@NotNull ServerRequest serverRequest) {
    return Mono.justOrEmpty(serverRequest.headers().contentType())
            .filter(mediaType -> mediaType.isCompatibleWith(MediaType.MULTIPART_FORM_DATA))
            .switchIfEmpty(Mono.error(ObjectNotFoundApiException::new))
            .flatMap(mediaType -> serverRequest.multipartData())
            .flatMap(multiPartData -> {
                Map<String, Part> parts = multiPartData.toSingleValueMap();
                Part filePart = parts.get("file");

                if (!(filePart instanceof FilePart)) {
                    return Mono.error(ObjectNotFoundApiException::new);
                }

                Map<String, String> metadata = parts.entrySet().stream()
                        .filter(entry -> !entry.getKey().equals("file"))
                        .filter(entry -> entry.getValue() instanceof FormFieldPart)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> ((FormFieldPart) entry.getValue()).value()
                        ));

                metadata.put("display_name", ((FilePart) filePart).filename());
                metadata.put("status", "active");

                return Mono.just(new ComplytFileDto(
                        (FilePart) filePart,
                        new ComplytFileMetadataDto(UUID.randomUUID(), metadata, null, null, null, null)
                ));
            });
}
}