package io.complyt.files.v1.validators.query_params;

import io.complyt.files.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Optional;

import static org.mockito.Mockito.when;

class QueryParamsExtractorFileTest {

    @Test
    void testExtract_validMultipartFormData() {
        // Arrange
        QueryParamsExtractorFile queryParamsExtractor = new QueryParamsExtractorFile();
        ServerRequest serverRequest = Mockito.mock(ServerRequest.class);
        ServerRequest.Headers headers = Mockito.mock(ServerRequest.Headers.class);

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.contentType()).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));

        FilePart filePart = Mockito.mock(FilePart.class);
        when(filePart.filename()).thenReturn("test-file.txt");

        FormFieldPart typePart = Mockito.mock(FormFieldPart.class);
        when(typePart.value()).thenReturn("document");

        MultiValueMap<String, Part> parts = new LinkedMultiValueMap<>();
        parts.add("file", filePart);
        parts.add("type", typePart);

        when(serverRequest.multipartData()).thenReturn(Mono.just(parts));

        // Act
        Mono<ComplytFileDto> result = queryParamsExtractor.extract(serverRequest);

        // Assert
        StepVerifier.create(result)
                .assertNext(complytFileDto -> {
                    ComplytFileMetadataDto metadataDto = complytFileDto.metadata();
                    assert metadataDto != null;
                    assert metadataDto.metadata().get("display_name").equals("test-file.txt");
                    assert metadataDto.metadata().get("type").equals("document");
                    assert metadataDto.metadata().get("status").equals("active");
                })
                .verifyComplete();
    }

    @Test
    void testExtract_filePartMissing() {
        // Arrange
        QueryParamsExtractorFile queryParamsExtractor = new QueryParamsExtractorFile();
        ServerRequest serverRequest = Mockito.mock(ServerRequest.class);
        ServerRequest.Headers headers = Mockito.mock(ServerRequest.Headers.class);

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.contentType()).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));

        MultiValueMap<String, Part> parts = new LinkedMultiValueMap<>();
        FormFieldPart typePart = Mockito.mock(FormFieldPart.class);
        when(typePart.value()).thenReturn("document");
        parts.add("type", typePart);

        when(serverRequest.multipartData()).thenReturn(Mono.just(parts));

        // Act
        Mono<ComplytFileDto> result = queryParamsExtractor.extract(serverRequest);

        // Assert
        StepVerifier.create(result)
                .expectError(ObjectNotFoundApiException.class)
                .verify();
    }

    @Test
    void testExtract_noMultipartFormData() {
        // Arrange
        QueryParamsExtractorFile queryParamsExtractor = new QueryParamsExtractorFile();
        ServerRequest serverRequest = Mockito.mock(ServerRequest.class);
        ServerRequest.Headers headers = Mockito.mock(ServerRequest.Headers.class);

        when(serverRequest.headers()).thenReturn(headers);
        when(headers.contentType()).thenReturn(Optional.of(MediaType.APPLICATION_JSON));

        // Act
        Mono<ComplytFileDto> result = queryParamsExtractor.extract(serverRequest);

        // Assert
        StepVerifier.create(result).expectError(); // Expect an empty Mono
    }
}