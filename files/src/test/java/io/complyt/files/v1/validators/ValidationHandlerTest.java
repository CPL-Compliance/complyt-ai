package io.complyt.files.v1.validators;

import io.complyt.files.business.storage.StorageWrapper;
import io.complyt.files.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.files.v1.exceptions.types.ObjectNotValidApiException;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import io.complyt.files.v1.models.FileDto;
import io.complyt.files.v1.validators.query_params.QueryParamsExtractor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest()
class ValidationHandlerTest {

    @Autowired
    SpringValidatorAdapter springValidatorAdapter;

    @Autowired
    ValidationHandler<FileDto, SpringValidatorAdapter> fileDtoValidationHandler;

    @Autowired
    ValidationHandler<ComplytFileDto, SpringValidatorAdapter> complytFileDtoValidationHandler;


    @MockBean
    ServerRequest serverRequest;

    @MockBean
    StorageWrapper storageWrapper;

    @Autowired
    QueryParamsExtractor<ComplytFileDto> queryParamsExtractor;

    @Test
    void handle_SaveFile_ValidRequest() {
        // Given
        ComplytFileDto complytFileDto = TestUtilities.createComplytFileDto();
        MultiValueMap<String, Part> formData = new LinkedMultiValueMap<>();
        formData.add("file", mock(FilePart.class));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);

        // When

        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));
        when(serverRequest.multipartData()).thenReturn(Mono.just(formData));
        when(serverRequest.bodyToMono(ComplytFileDto.class)).thenReturn(Mono.just(complytFileDto));

        Mono<ComplytFileDto> complytFileDtoMono = complytFileDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(complytFileDtoMono).expectNextMatches(actualDto -> {
            // Check common fields
            assertEquals(complytFileDto.file().content(), actualDto.file().content());
            assertEquals(TestUtilities.tenantId, actualDto.metadata().withTenantId(TestUtilities.tenantId).tenantId());
            assertEquals("active", actualDto.metadata().metadata().get("status"));

            // Further specific field checks
            return true;
        }).verifyComplete();
    }

    @Test
    void handle_SaveFileNoFile_returnsError() {
        // Given
        ComplytFileDto complytFileDto = new ComplytFileDto(null, null);
        MultiValueMap<String, Part> formData = new LinkedMultiValueMap<>();
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);

        // When

        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));
        when(serverRequest.multipartData()).thenReturn(Mono.just(formData));
        when(serverRequest.bodyToMono(ComplytFileDto.class)).thenReturn(Mono.just(complytFileDto));


        Mono<ComplytFileDto> complytFileDtoMono = complytFileDtoValidationHandler.handle(serverRequest);

        // Then
        StepVerifier.create(complytFileDtoMono).verifyError(ObjectNotValidApiException.class);
    }


    @Test
    void validate_validFile_returnsFileDto() {
        FileDto fileDto = TestUtilities.createFileDto();
        when(serverRequest.bodyToMono(FileDto.class)).thenReturn(Mono.just(fileDto));
        Mono<FileDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectNext(fileDto).verifyComplete();
    }

    @Test
    void validate_invalidFileDto_returnsError() {
        FileDto fileDto = TestUtilities.createFileDto().withLink("");
        when(serverRequest.bodyToMono(FileDto.class)).thenReturn(Mono.just(fileDto));
        Mono<FileDto> validationMono = fileDtoValidationHandler.validate(serverRequest);

        StepVerifier.create(validationMono).expectError().verify();
    }

    @Test
    void handle_InvalidObjectWithValidFile_LogsErrorAndReturnsMonoError() {
        // Given
        ComplytFileDto invalidComplytFileDto = new ComplytFileDto(null, new ComplytFileMetadataDto(null, null, null, null, null, null));
        ServerRequest.Headers headersMock = mock(ServerRequest.Headers.class);
        MultiValueMap<String, Part> formData = new LinkedMultiValueMap<>();
        formData.add("file", null);

        when(serverRequest.headers()).thenReturn(headersMock);
        when(serverRequest.headers().contentType()).thenReturn(Optional.of(MediaType.MULTIPART_FORM_DATA));
        when(serverRequest.multipartData()).thenReturn(Mono.just(formData));
        when(serverRequest.bodyToMono(ComplytFileDto.class)).thenReturn(Mono.just(invalidComplytFileDto));


        // When
        Mono<ComplytFileDto> complytFileDtoMono = complytFileDtoValidationHandler.handle(serverRequest);

        StepVerifier.create(complytFileDtoMono).expectError(Exception.class).verify();
    }

}