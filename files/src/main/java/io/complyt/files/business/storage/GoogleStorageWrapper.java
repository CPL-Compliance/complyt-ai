package io.complyt.files.business.storage;

import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.PageImpl;
import com.google.cloud.storage.*;
import io.complyt.files.business.storage.wrappers.StorageWrapperBase;
import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.utils.observability.ContextLogger;
import io.complyt.files.v1.exceptions.types.ComplytApiException;
import io.complyt.files.v1.exceptions.types.ObjectNotFoundApiException;
import io.complyt.files.v1.routers.FileRouter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

@Slf4j
@NoArgsConstructor
public class GoogleStorageWrapper extends StorageWrapperBase {

    private Storage storage;
    private String bucketName;
    private int urlTtl;


    public GoogleStorageWrapper(String serviceAccountKey, int urlTtl, String bucketName, String projectId) throws IOException {
        super();
        this.storage = StorageOptions.newBuilder()
                .setProjectId(projectId)
                .setCredentials(ServiceAccountCredentials.fromStream(
                        new ByteArrayInputStream(serviceAccountKey.getBytes(StandardCharsets.UTF_8))))
                .build()
                .getService();
        this.bucketName = bucketName;
        this.urlTtl = urlTtl;
    }

    public GoogleStorageWrapper(int urlTtl, String gcsUrl, String bucketName, String projectId) {

        this.storage = StorageOptions.newBuilder()
                .setHost(gcsUrl)
                .setProjectId(projectId)
                .setCredentials(NoCredentials.getInstance())
                .build()
                .getService();
        this.bucketName = bucketName;
        this.urlTtl = urlTtl;

    }

    public GoogleStorageWrapper(Storage storage, int urlTtl, String bucketName) {
        super();
        this.storage = storage;
        this.bucketName = bucketName;
        this.urlTtl = urlTtl;
    }

    @Override
    public Flux<ComplytFileMetadata> listFilesInTenant(String tenantId, String files_status_query) {
        return ContextLogger.observeCtx("GoogleStorageWrapper -> listFilesInTenant " + tenantId, log::info).thenMany(getAllFilesInTenantByStatus(tenantId, files_status_query)
                .flatMap(blob -> tryCreateFileMetadataObject(blob, tenantId))
                .switchIfEmpty(Flux.empty()));
    }

    @Override
    public Flux<ComplytFileMetadata> listFilesWithLinkInTenant(String tenantId, String files_status_query) {
        return ContextLogger.observeCtx("GoogleStorageWrapper -> listFilesWithLinkInTenant " + tenantId, log::info).thenMany(getAllFilesInTenantByStatus(tenantId, files_status_query)
                .flatMap(blob -> tryCreateFileMetadataWithSignedLinkObject(blob, tenantId))
                .switchIfEmpty(Flux.empty()));
    }

    @Override
    public Mono<ComplytFileMetadata> saveFile(ComplytFile complytFile) {
        StringBuilder fullPathBuilder = new StringBuilder();
        fullPathBuilder.append(complytFile.getMetadata().tenantId())
                .append("/")
                .append(complytFile.getMetadata().complytId())
                .append(".")
                .append(extractFileExtension(complytFile.getFile().filename()));

        String fullPath = fullPathBuilder.toString();
        BlobId blobId = BlobId.of(bucketName, fullPath);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentDisposition("filename=" + complytFile.getFile().filename()).setMetadata(complytFile.getMetadata().metadata()).build();
        return ContextLogger.observeCtx("GoogleStorageWrapper -> saveFile " + fullPath, log::info).then(fileContentToByteArray(complytFile.getFile().content())
                .flatMap(bytes -> Mono.fromCallable(() -> storage.create(blobInfo, bytes)))
                .map(blob -> new ComplytFileMetadata(
                        complytFile.getMetadata().complytId(),
                        complytFile.getMetadata().metadata(),
                        complytFile.getMetadata().tenantId(),
                        blob.getUpdateTimeOffsetDateTime(),
                        blob.getCreateTimeOffsetDateTime(),
                        FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytFile.getMetadata().complytId()
                ))
                .doOnError(Mono::error)
                .switchIfEmpty(Mono.error(new ComplytApiException("Error"))));
    }


    @Override
    public Mono<ComplytFileMetadata> getSignedLinkForFile(UUID complytId, String tenantId) {
        String path = tenantId + "/" + complytId;
        return ContextLogger.observeCtx("GoogleStorageWrapper -> getSignedLinkForFile", log::info).then(Mono.justOrEmpty(getOneFile(path))
                .flatMap(blob -> Mono.just(new ComplytFileMetadata(extractUUID(blob.getName()),
                        blob.getMetadata(), tenantId, blob.getUpdateTimeOffsetDateTime(),
                        blob.getCreateTimeOffsetDateTime(),
                        createSignedLinkForBlob(blob))))
                .doOnError(Mono::error)
                .switchIfEmpty(ContextLogger.observeCtx("ObjectNotFoundApiException thrown in GoogleStorageWrapper.getSignedLinkForFile for file with complytId " + complytId + " and tenantId " + tenantId, log::error)
                        .then(Mono.error(new ObjectNotFoundApiException()))));
    }

    @Override
    public Mono<ComplytFileMetadata> markAsDeleted(UUID complytId, String tenantId) {
        String path = tenantId + "/" + complytId;
        return ContextLogger.observeCtx("GoogleStorageWrapper -> markAsDeleted" + path, log::info).then(Mono.justOrEmpty(getOneFile(path))
                .flatMap(blob -> updateFileMetadata(blob, "status", "deleted")
                        .flatMap(updatedBlob -> Mono.just(new ComplytFileMetadata(
                                extractUUID(updatedBlob.getName()),
                                updatedBlob.getMetadata(),
                                tenantId,
                                updatedBlob.getUpdateTimeOffsetDateTime(),
                                updatedBlob.getCreateTimeOffsetDateTime(),
                                FileRouter.COMPLYT_FILE_BASE_URL + "/" + complytId))))
                .doOnError(Mono::error)
                .switchIfEmpty(ContextLogger.observeCtx("ObjectNotFoundApiException thrown in GoogleStorageWrapper.markAsDeleted for file with complytId " + complytId + " and tenantId " + tenantId, log::error)
                        .then(Mono.error(new ObjectNotFoundApiException()))));
    }

    private Mono<byte[]> fileContentToByteArray(Flux<DataBuffer> content) {
        return DataBufferUtils.join(content)
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    return bytes;
                });
    }

    private String extractFileExtension(String filename) {
        String[] parts = filename.split("\\.");
        return parts.length > 1 ? parts[1] : "";
    }

    private String createSignedLinkForBlob(Blob blob) {
        BlobId blobId = blob.getBlobId();
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        URL signedUrl = storage.signUrl(blobInfo, this.urlTtl, TimeUnit.MINUTES,
                Storage.SignUrlOption.withV4Signature());
        return signedUrl.toString();
    }

    private UUID extractUUID(String input) {
        // Regular expression to match a UUID
        String regex = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return UUID.fromString(matcher.group());
        }
        return null; // Return null if no UUID is found
    }

    private Mono<ComplytFileMetadata> tryCreateFileMetadataObject(Blob blob, String tenantId) {
        UUID blobUUID = extractUUID(blob.getName());
        return blobUUID != null ? Mono.just(new ComplytFileMetadata(extractUUID(blob.getName()), blob.getMetadata(),
                tenantId, blob.getUpdateTimeOffsetDateTime(), blob.getCreateTimeOffsetDateTime(),
                FileRouter.COMPLYT_FILE_BASE_URL + "/" + blobUUID)) : Mono.empty();
    }

    private Mono<ComplytFileMetadata> tryCreateFileMetadataWithSignedLinkObject(Blob blob, String tenantId) {
        UUID blobUUID = extractUUID(blob.getName());
        return blobUUID != null ? Mono.just(new ComplytFileMetadata(extractUUID(blob.getName()), blob.getMetadata(),
                tenantId, blob.getUpdateTimeOffsetDateTime(), blob.getCreateTimeOffsetDateTime(),
                createSignedLinkForBlob(blob))) : Mono.empty();
    }

    private Blob getOneFile(String path) {
        return StreamSupport.stream(
                        storage.list(bucketName, Storage.BlobListOption.prefix(path),
                                        Storage.BlobListOption.includeFolders(false))
                                .iterateAll().spliterator(), false)
                .findFirst()
                .orElse(null);
    }

    private Mono<Blob> updateFileMetadata(Blob blob, String key, String value) {
        Map<String, String> newMetadata = new HashMap<>(blob.getMetadata());
        newMetadata.put(key, value);
        Blob updatedBlob = blob.toBuilder().setMetadata(newMetadata).build()
                .update(Storage.BlobTargetOption.generationMatch());
        return Mono.justOrEmpty(updatedBlob);
    }

    private Flux<Blob> getAllFilesInTenantByStatus(String tenantId, String files_status_query) {
        PageImpl p = (PageImpl) storage.list(bucketName, Storage.BlobListOption.prefix(tenantId + "/"),
                Storage.BlobListOption.includeFolders(false));
        return Flux.fromIterable(() -> storage.list(bucketName, Storage.BlobListOption.prefix(tenantId + "/"),
                                Storage.BlobListOption.includeFolders(false))
                        .iterateAll()
                        .iterator())
                .filter(blob -> blob.getMetadata().get("status").equals(files_status_query));
    }
}
