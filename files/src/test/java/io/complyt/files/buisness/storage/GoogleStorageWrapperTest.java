package io.complyt.files.buisness.storage;

import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.PageImpl;
import com.google.cloud.storage.*;
import io.complyt.files.business.storage.GoogleStorageWrapper;
import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.v1.exceptions.types.ComplytApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GoogleStorageWrapperTest {

    @InjectMocks
    private GoogleStorageWrapper googleStorageWrapper;

    @Mock
    private Storage storage;

    @Mock
    private Blob blob;

    @Mock
    private Blob.Builder blobBuilder;
    private ComplytFile complytFile;

    @BeforeEach
    public void setUp() throws IOException {
        googleStorageWrapper = TestUtilities.initGoogleStorageWrapper(storage);
        complytFile = TestUtilities.createComplytFile();
    }

    @Test
    public void saveFile_ValidFile_Success() {
        // When
        when(complytFile.getFile().filename()).thenReturn("test-file.txt");
        when(complytFile.getFile().content()).thenReturn(Flux.just(new DefaultDataBufferFactory().wrap("test-content".getBytes())));
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mock(Blob.class));
        Mono<ComplytFileMetadata> complytFileMetadataMono = googleStorageWrapper.saveFile(complytFile);

        StepVerifier.create(complytFileMetadataMono)
                .expectNext(complytFile.getMetadata().withCreateTime(null).withUpdateTime(null))
                .verifyComplete();
    }

    @Test
    public void saveFile_FileNameWithoutDot_Success() {
        // When
        when(complytFile.getFile().filename()).thenReturn("test-file");
        when(complytFile.getFile().content()).thenReturn(Flux.just(new DefaultDataBufferFactory().wrap("test-content".getBytes())));
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mock(Blob.class));
        Mono<ComplytFileMetadata> complytFileMetadataMono = googleStorageWrapper.saveFile(complytFile);

        StepVerifier.create(complytFileMetadataMono)
                .expectNext(complytFile.getMetadata().withCreateTime(null).withUpdateTime(null))
                .verifyComplete();
    }

    @Test
    public void saveFile_InvalidFile_Failure() {
        when(complytFile.getFile().filename()).thenReturn("test-file.txt");
        when(complytFile.getFile().content()).thenReturn(Flux.just(new DefaultDataBufferFactory().wrap("test-content".getBytes())));
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(null);
        Mono<ComplytFileMetadata> complytFileMetadataMono = googleStorageWrapper.saveFile(complytFile);

        StepVerifier.create(complytFileMetadataMono)
                .expectError(ComplytApiException.class)
                .verify();
    }

    @Test
    public void getSignedLink_ValidFile_Success() throws MalformedURLException {
        // Given
        PageImpl p = TestUtilities.initGoogleStorageListWithLinkResponse(complytFile.getMetadata());
        // When
        when(storage.list(any(String.class), any(Storage.BlobListOption.class))).thenReturn(p);

        when(storage.signUrl(any(), any(long.class), any(), any(Storage.SignUrlOption.class))).thenReturn(new URL("https://test.com"));
        Mono<ComplytFileMetadata> complytFileMetadataMono = googleStorageWrapper
                .getSignedLinkForFile(complytFile.getMetadata().complytId(), TestUtilities.tenantId);


        StepVerifier.create(complytFileMetadataMono).expectNext(complytFile.getMetadata().withLink("https://test.com").withCreateTime(null).withUpdateTime(null)).verifyComplete();
    }

    @Test
    public void getSignedLink_InvalidFile_Failure() {
        // Given
        PageImpl p = TestUtilities.initGoogleStorageListWithLinkResponseNoUUID(complytFile.getMetadata());
        // When
        when(storage.list(any(String.class), any(Storage.BlobListOption.class))).thenReturn(p);

        when(storage.signUrl(any(), any(long.class), any(), any())).thenThrow(StorageException.class);
        Mono<ComplytFileMetadata> complytFileMetadataMono = googleStorageWrapper
                .getSignedLinkForFile(null, null);

        StepVerifier.create(complytFileMetadataMono)
                .expectError(StorageException.class)
                .verify();
    }


    @Test
    public void listFilesInTenant_ValidQueryParam_Success() {
        // Given
        PageImpl p = TestUtilities.initGoogleStorageListResponse(complytFile.getMetadata());
        // When
        when(storage.list(any(String.class), any(Storage.BlobListOption.class))).thenReturn(p);

        Flux<ComplytFileMetadata> complytFileMetadataFlux = googleStorageWrapper
                .listFilesInTenant(TestUtilities.tenantId, "active");

        StepVerifier.create(complytFileMetadataFlux)
                .expectNext(complytFile.getMetadata().withCreateTime(null).withUpdateTime(null))
                .verifyComplete();
    }

    @Test
    public void listFilesInTenant_AllFilesWithoutUUID_ReturnEmptySuccess() {
        // Given
        PageImpl p = TestUtilities.initGoogleStorageListWithLinkResponseNoUUIDForGetAll(complytFile.getMetadata());
        // When
        when(storage.list(any(String.class), any(Storage.BlobListOption.class))).thenReturn(p);

        Flux<ComplytFileMetadata> complytFileMetadataFlux = googleStorageWrapper
                .listFilesInTenant(TestUtilities.tenantId, "active");

        StepVerifier.create(complytFileMetadataFlux)
                .expectNext()
                .verifyComplete();
    }

    @Test
    public void listFilesWithLinkInTenant_ValidQueryParam_Success() throws MalformedURLException {
        // Given
        PageImpl p = TestUtilities.initGoogleStorageListWithLinkResponse(complytFile.getMetadata());
        // When
        when(storage.list(any(String.class), any(Storage.BlobListOption.class))).thenReturn(p);

        when(storage.signUrl(any(), any(long.class), any(), any())).thenReturn(new URL("https://google.com/"));
        Flux<ComplytFileMetadata> complytFileMetadataFlux = googleStorageWrapper
                .listFilesWithLinkInTenant(TestUtilities.tenantId, "active");

        StepVerifier.create(complytFileMetadataFlux)
                .expectNext(complytFile.getMetadata().withCreateTime(null).withUpdateTime(null).withLink("https://google.com/"))
                .verifyComplete();
    }

    @Test
    public void listFilesWithLinkInTenant_AllFilesWithoutUUID_ReturnEmptySuccess() throws MalformedURLException {
        // Given
        PageImpl p = TestUtilities.initGoogleStorageListWithLinkResponseNullName(complytFile.getMetadata());
        // When
        when(storage.list(any(String.class), any(Storage.BlobListOption.class))).thenReturn(p);

        Flux<ComplytFileMetadata> complytFileMetadataFlux = googleStorageWrapper
                .listFilesWithLinkInTenant(TestUtilities.tenantId, "active");

        StepVerifier.create(complytFileMetadataFlux).verifyComplete();
    }

    @Test
    void markAsDeleted_FileExists_UpdatesMetadata() {
        // Arrange
        String tenantId = complytFile.getMetadata().tenantId();
        UUID compytId = complytFile.getMetadata().complytId();
        String complytIdString = compytId.toString();
        String path = tenantId + "/" + complytIdString;
        Map<String, String> metadata = new HashMap<>();
        metadata.put("status", "deleted");

        when(blob.getName()).thenReturn(path);
        when(blob.getMetadata()).thenReturn(metadata);
        when(blob.toBuilder()).thenReturn(blobBuilder);
        when(blobBuilder.setMetadata(anyMap())).thenReturn(blobBuilder);
        when(blobBuilder.build()).thenReturn(blob);
        when(blob.update(any(Storage.BlobTargetOption.class))).thenReturn(blob);

        Page<Blob> page = mock(Page.class);
        when(page.iterateAll()).thenReturn(() -> java.util.Collections.singletonList(blob).iterator());
        when(storage.list(anyString(), any(Storage.BlobListOption.class))).thenReturn(page);

        // Act
        Mono<ComplytFileMetadata> complytFileMetadataMono = googleStorageWrapper.markAsDeleted(compytId, tenantId);

        // Assert
        StepVerifier.create(complytFileMetadataMono)
                .expectNextMatches(complytFileMetadata ->
                        "deleted".equals(complytFileMetadata.metadata().get("status"))
                )
                .verifyComplete();
    }

    @Test
    void constructor_WithServiceAccountKey_InitializesCorrectly() throws IOException {
        // Arrange
        String serviceAccountKey = "{\"type\":\"service_account\",\"project_id\":\"test-project\"}";
        int urlTtl = 60;
        String bucketName = "test-bucket";
        String projectId = "test-project";

        StorageOptions.Builder builder = mock(StorageOptions.Builder.class);
        StorageOptions storageOptions = mock(StorageOptions.class);
        Storage storage = mock(Storage.class);

        try (MockedStatic<StorageOptions> mockedStorageOptions = mockStatic(StorageOptions.class);
             MockedStatic<ServiceAccountCredentials> mockedServiceAccountCredentials = mockStatic(ServiceAccountCredentials.class)) {

            mockedStorageOptions.when(StorageOptions::newBuilder).thenReturn(builder);
            when(builder.setProjectId(projectId)).thenReturn(builder);
            when(builder.setCredentials(any(ServiceAccountCredentials.class))).thenReturn(builder);
            when(builder.build()).thenReturn(storageOptions);
            when(storageOptions.getService()).thenReturn(storage);

            mockedServiceAccountCredentials.when(() -> ServiceAccountCredentials.fromStream(any())).thenReturn(mock(ServiceAccountCredentials.class));

            // Act
            GoogleStorageWrapper wrapper = new GoogleStorageWrapper(serviceAccountKey, urlTtl, bucketName, projectId);

            // Assert
            assertNotNull(wrapper);
            // You can add more assertions here if the class has public methods to access its properties
        }
    }

    @Test
    void constructor_WithServiceAccountKey_AndUrl_ForIngTest_InitializesCorrectly() throws IOException {
        // Arrange
        String serviceAccountKey = "{\"type\":\"service_account\",\"project_id\":\"test-project\"}";
        int urlTtl = 60;
        String bucketName = "test-bucket";
        String projectId = "test-project";
        String gcsUrl = "http://localhost:4443";
        StorageOptions.Builder builder = mock(StorageOptions.Builder.class);
        StorageOptions storageOptions = mock(StorageOptions.class);
        Storage storage = mock(Storage.class);

        try (MockedStatic<StorageOptions> mockedStorageOptions = mockStatic(StorageOptions.class);
             MockedStatic<ServiceAccountCredentials> mockedServiceAccountCredentials = mockStatic(ServiceAccountCredentials.class);
             MockedStatic<NoCredentials> mockedNoCredentials = mockStatic(NoCredentials.class)) {

            mockedStorageOptions.when(StorageOptions::newBuilder).thenReturn(builder);
            when(builder.setProjectId(projectId)).thenReturn(builder);
            when(builder.setHost(gcsUrl)).thenReturn(builder);
            when(builder.build()).thenReturn(storageOptions);
            when(storageOptions.getService()).thenReturn(storage);

            // Handle the NoCredentials case when gcsUrl is provided
            NoCredentials noCredentials = NoCredentials.getInstance();
            mockedNoCredentials.when(NoCredentials::getInstance).thenReturn(noCredentials);
            when(builder.setCredentials(noCredentials)).thenReturn(builder);

            // Handle the ServiceAccountCredentials case when no gcsUrl is provided
            ServiceAccountCredentials serviceAccountCredentials = mock(ServiceAccountCredentials.class);
            mockedServiceAccountCredentials.when(() -> ServiceAccountCredentials.fromStream(any())).thenReturn(serviceAccountCredentials);

            // Act
            GoogleStorageWrapper wrapper = new GoogleStorageWrapper(urlTtl, gcsUrl, bucketName, projectId);

            // Assert
            assertNotNull(wrapper);
            // You can add more assertions here if the class has public methods to access its properties
        }
    }

    @Test
    void constructor_WithStorage_InitializesCorrectly() {
        // Arrange
        Storage storage = mock(Storage.class);
        int urlTtl = 60;
        String bucketName = "test-bucket";

        // Act
        GoogleStorageWrapper wrapper = new GoogleStorageWrapper(storage, urlTtl, bucketName);

        // Assert
        assertNotNull(wrapper);
        // You can add more assertions here if the class has public methods to access its properties
    }
}