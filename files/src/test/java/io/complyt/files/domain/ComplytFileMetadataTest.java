package io.complyt.files.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ComplytFileMetadataTest {
    private ComplytFileMetadata complytFileMetadata;

    @BeforeEach
    public void setUp() {
        complytFileMetadata = TestUtilities.createComplytFileMetadata();
    }

    @Test
    public void equals_SameFileMetadata_ReturnsTrue() {
        // Given
        ComplytFileMetadata referanceComplytFileMetadata = TestUtilities.createComplytFileMetadata(
                complytFileMetadata.complytId(),
                complytFileMetadata.metadata(),
                complytFileMetadata.tenantId(),
                complytFileMetadata.updateTime(),
                complytFileMetadata.createTime(),
                complytFileMetadata.link());
        // Then
        assertEquals(complytFileMetadata, referanceComplytFileMetadata);
    }

    @Test
    public void toString_ReturnsString() {
        // Given
        String expectedString = "ComplytFileMetadata[complytId=" + complytFileMetadata.complytId().toString() +
                ", metadata=" + complytFileMetadata.metadata().toString() +
                ", tenantId=" + complytFileMetadata.tenantId() +
                ", updateTime=" + complytFileMetadata.updateTime() +
                ", createTime=" + complytFileMetadata.createTime() +
                ", link=" + complytFileMetadata.link() + "]";

        // When
        String actualString = complytFileMetadata.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}

