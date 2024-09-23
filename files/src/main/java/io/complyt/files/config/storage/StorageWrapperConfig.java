package io.complyt.files.config.storage;

import io.complyt.files.annotations.Generated;
import io.complyt.files.business.storage.GoogleStorageWrapper;
import io.complyt.files.business.storage.wrappers.StorageWrapperBase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Generated
public class StorageWrapperConfig {

    @Profile({"stubStorage", "default"})
    @Bean("storageWrapper")
    public StorageWrapperBase.StubStorageWrapper stubStorageWrapper() {
        return new StorageWrapperBase.StubStorageWrapper();
    }

    @Profile({"googleStorage"})
    @Bean("storageWrapper")
    public GoogleStorageWrapper googleStorageWrapper(   @Value("${key}") String serviceAccountKey,
                                                        @Value("${url-ttl}") int urlTtl,
                                                        @Value("${bucket-name}") String bucketName,
                                                        @Value("${project-id}") String projectId) throws IOException {
        return new GoogleStorageWrapper(serviceAccountKey, urlTtl, bucketName, projectId);
    }

    @Profile({"fakeGoogleStorage"})
    @Bean("storageWrapper")
    public GoogleStorageWrapper fakeGoogleStorage(   @Value("${google.storage.url}") String gcsUrl,
                                                        @Value("${key}") String serviceAccountKey,
                                                        @Value("${url-ttl}") int urlTtl,
                                                        @Value("${bucket-name}") String bucketName,
                                                        @Value("${project-id}") String projectId) {
        return new GoogleStorageWrapper(urlTtl, gcsUrl, bucketName, projectId);
    }

}
