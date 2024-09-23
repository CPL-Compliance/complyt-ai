package testUtils;

import com.google.cloud.PageImpl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import io.complyt.files.business.storage.GoogleStorageWrapper;
import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.domain.File;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import io.complyt.files.v1.models.FileDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mockito.Mockito;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.OffsetDateTime;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public interface TestUtilities {
    String linkStr = "https://youtu.be/dQw4w9WgXcQ";
    String tenantId = "org_SttAcBkK7b32w7kA";
    String bucketName = "files-unit-tests";

    String serviceAccountKey = "{\n" +
            "  \"type\": \"service_account\",\n" +
            "  \"project_id\": \"project-id\",\n" +
            "  \"private_key_id\": \"private-key-id\",\n" +
            "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\MIIEvQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBALdCw2A2YEDXZ7CqsDAf8F0FSRmQQFGyDGz1Ks6/AIg7FyNC3Cg3kTzTmjEReJ3IhDgGk4I4pXrtXNzgfPo5yw3B3/H1ZB7PZCVjJ5JmUbHp6ayR9p7fdx+U3OaXZYPfWyIr6Vlj06XYfPi7ARbOiN/w6I0KKURyBJZjQkLhXAgMBAAECgYEAlmzz/ukZm3QroYOHBCwXa4oCdskQIQVqSn+h5zlvN4vUe/XAxFlOzqu8Iz9m+hE7hb9/v+ZQ/NdAcmw4GVOb1MmPYdl7Z26hT5JxF54JlDo3ZpTXOzKRTPrWsjTSuG2KOR7z3R2z/L1B7mF6w5yppvZ5MTI1ZzV/JlYFfJ/BoeoYECQQDshZmBiwx4vsaxYj6DhvMggfDiwI4RPgJveQCLtKlBfMlxgMt+uAW0QXjLXYcU3owzVXy20ZMeEnFWybOeS+IOrAkEA0lPyJkH+5hyYfgjBqbg4tBZr3lKZTNN43hLrEIZzbhWSbAKV/7G3pk4SZqRGaFw+gZTNeRbNBQpS+JMmVS3lwJBALFjbyR9Bl2AduD+Q7/y2Ub5QzYz7oGqVrMbDzvZNUxz0Uqtnrf5W5wAG2OFhZHZ7ElbYINZNHqNyVtt3ExpsbkCQBmNxVxY9TxJdyHt7gROAcAxKmD4I/h1F/mcZC5FZk5vpeNhtkHahf7OwXDJdw5b3b25I6mOBD/N8fzpYHGRe6kCQQDTCjo4x0DkAb6svB5WVTLj7/Wt6g9B2BYUNXXhNWz42Er06vQysahcIfIC5CRNj6cFdyxFNR0doSwROUcfbq8A\\n-----END PRIVATE KEY-----\\n\",\n" +
            "  \"client_email\": \"your-service-account@project-id.iam.gserviceaccount.com\",\n" +
            "  \"client_id\": \"client-id\",\n" +
            "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
            "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
            "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
            "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/your-service-account%40project-id.iam.gserviceaccount.com\"\n" +
            "}";

    static Jwt.Builder stubJwt() {
        return Jwt.withTokenValue("token")
                .header("typ", "JWT")
                .claim("tenant_id", "it_tenant");
    }

    static File createFile() {
        return new File(UUID.randomUUID(), ObjectId.get().toString(), tenantId, linkStr);
    }

    static File createFile(UUID complytId, String id) {
        return new File(complytId, id, tenantId, linkStr);
    }

    static FileDto createFileDto() {
        return new FileDto(UUID.randomUUID(), linkStr);
    }

    static FileDto createFileDto(UUID complytId) {
        return new FileDto(complytId, linkStr);
    }

    static Document fileDocument() {
        return new Document()
                .append("complytId", UUID.randomUUID().toString().getBytes()) // UUID as binary data
                .append("_id", new ObjectId("65b6a7f8f930555db9c7c246"))  // Typically this would be set automatically by MongoDB if using ObjectIds
                .append("tenantId", "tenantIdExample")
                .append("link", "http://example.com/token");
    }

    static ComplytFileMetadata createComplytFileMetadata() {
        UUID complytId = UUID.randomUUID();
        return new ComplytFileMetadata(complytId, new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "active")), tenantId, OffsetDateTime.now(), OffsetDateTime.now().minusDays(1), "/v1/files/" + complytId.toString());
    }

    static ComplytFileMetadata createComplytFileMetadata(UUID complytId, Map<String, String> metadata, String tenantId, OffsetDateTime updateTime, OffsetDateTime createTime, String link) {
        return new ComplytFileMetadata(complytId, metadata, tenantId, updateTime, createTime, link);
    }

    static ComplytFile createComplytFile() {
        FilePart filePart = Mockito.mock(FilePart.class);
        return new ComplytFile(filePart, createComplytFileMetadata());
    }

    static ComplytFile createComplytFile(FilePart filePart, ComplytFileMetadata complytFileMetadata) {
        return new ComplytFile(filePart, complytFileMetadata);
    }


    static ComplytFileMetadataDto createComplytFileMetadataDto() {
        UUID complytId = UUID.randomUUID();
        return new ComplytFileMetadataDto(complytId, new HashMap<>(Map.of("type", "document", "display_name", "test.pdf")), tenantId, OffsetDateTime.now(), OffsetDateTime.now().minusDays(1), "/v1/files/" + complytId.toString());
    }

    static ComplytFileMetadataDto createComplytFileMetadataDto(UUID complytId, Map<String, String> metadata, String tenantId, OffsetDateTime updateTime, OffsetDateTime createTime, String link) {
        return new ComplytFileMetadataDto(complytId, metadata, tenantId, updateTime, createTime, link);
    }

    static ComplytFileDto createComplytFileDto() {
        FilePart filePart = Mockito.mock(FilePart.class);
        return new ComplytFileDto(filePart, createComplytFileMetadataDto());
    }

    static ComplytFileDto createComplytFileDto(FilePart filePart, ComplytFileMetadataDto complytFileMetadataDto) {
        return new ComplytFileDto(filePart, complytFileMetadataDto);
    }

    static GoogleStorageWrapper initGoogleStorageWrapper(Storage storage) {
        int urlTtl = 1;
        String bucketName = "Mockito.mock(String.class)";
        return new GoogleStorageWrapper(storage, urlTtl, bucketName);
    }

    static PageImpl initGoogleStorageListResponse(ComplytFileMetadata complytFileMetadata) {
        Blob blob1 = Mockito.mock(Blob.class);
        Mockito.when(blob1.getName()).thenReturn(complytFileMetadata.tenantId() + "/" + complytFileMetadata.complytId() + ".pdf");
        Mockito.when(blob1.getMetadata()).thenReturn(new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "active")));
        // Create a list of mocked Blob objects
        List<Blob> blobs = Arrays.asList(blob1);

        // Create a PageImpl instance
        PageImpl<Blob> page = new PageImpl<>(null, null, blobs);


        return page;
    }

    static PageImpl initGoogleStorageListWithLinkResponse(ComplytFileMetadata complytFileMetadata) {
        Blob blob1 = Mockito.mock(Blob.class);
        Mockito.when(blob1.getName()).thenReturn(complytFileMetadata.tenantId() + "/" + complytFileMetadata.complytId() + ".pdf");
        Mockito.when(blob1.getMetadata()).thenReturn(new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "active")));
        Mockito.when(blob1.getBlobId()).thenReturn(BlobId.of("", "", 3456789L));
        // Create a list of mocked Blob objects
        List<Blob> blobs = Arrays.asList(blob1);

        // Create a PageImpl instance
        PageImpl<Blob> page = new PageImpl<>(null, null, blobs);


        return page;
    }

    static PageImpl initGoogleStorageListWithLinkResponseNullName(ComplytFileMetadata complytFileMetadata) {
        Blob blob1 = Mockito.mock(Blob.class);
        Mockito.when(blob1.getName()).thenReturn("name");
        Mockito.when(blob1.getMetadata()).thenReturn(new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "active")));
        // Create a list of mocked Blob objects
        List<Blob> blobs = Arrays.asList(blob1);

        // Create a PageImpl instance
        PageImpl<Blob> page = new PageImpl<>(null, null, blobs);


        return page;
    }

    static PageImpl initGoogleStorageListWithLinkResponseNoUUID(ComplytFileMetadata complytFileMetadata) {
        Blob blob1 = Mockito.mock(Blob.class);
        Mockito.when(blob1.getName()).thenReturn("/" + ".pdf");
        Mockito.when(blob1.getMetadata()).thenReturn(new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "active")));
        Mockito.when(blob1.getBlobId()).thenReturn(BlobId.of("", "", 3456789L));
        // Create a list of mocked Blob objects
        List<Blob> blobs = Arrays.asList(blob1);

        // Create a PageImpl instance
        PageImpl<Blob> page = new PageImpl<>(null, null, blobs);


        return page;
    }

    static PageImpl initGoogleStorageListWithLinkResponseNoUUIDForGetAll(ComplytFileMetadata complytFileMetadata) {
        Blob blob1 = Mockito.mock(Blob.class);
        Mockito.when(blob1.getName()).thenReturn("/" + ".pdf");
        Mockito.when(blob1.getMetadata()).thenReturn(new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "active")));
//        Mockito.when(blob1.getBlobId()).thenReturn(BlobId.of("","",3456789L));
        // Create a list of mocked Blob objects
        List<Blob> blobs = Arrays.asList(blob1);

        // Create a PageImpl instance
        PageImpl<Blob> page = new PageImpl<>(null, null, blobs);


        return page;
    }

    static Blob initGoogleStorageBlob(ComplytFileMetadata complytFileMetadata) {
        Blob blob = Mockito.mock(Blob.class);
        when(blob.getName()).thenReturn(complytFileMetadata.tenantId() + "/" + complytFileMetadata.complytId() + ".pdf");
        when(blob.getMetadata()).thenReturn(new HashMap<>(Map.of("type", "document", "display_name", "test.pdf", "status", "active")));
        when(blob.getBlobId()).thenReturn(BlobId.of("", "", 3456789L));
        when(blob.toBuilder()).thenReturn(mock(Blob.Builder.class));
        return blob;
    }
}

