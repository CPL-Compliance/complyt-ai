package io.complyt.files.v1.mappers;

import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ComplytFileMapperImplTest {

    // Subclass to expose the protected method for testing
    private static class TestComplytFileMapperImpl extends ComplytFileMapperImpl {
        @Override
        public ComplytFileMetadata complytFileMetadataDtoToComplytFileMetadata(ComplytFileMetadataDto complytFileMetadataDto) {
            return super.complytFileMetadataDtoToComplytFileMetadata(complytFileMetadataDto);
        }
    }

    private final TestComplytFileMapperImpl mapper = new TestComplytFileMapperImpl();

    @Test
    void testComplytFileToComplytFileDtoWithNull() {
        // Test that null input returns null output
        ComplytFileDto result = mapper.complytFileToComplytFileDto(null);
        assertNull(result, "Expected null when input is null");
    }

    @Test
    void testComplytFileDtoToComplytFileWithNull() {
        // Test that null input returns null output
        ComplytFile result = mapper.complytFileDtoToComplytFile(null);
        assertNull(result, "Expected null when input is null");
    }

    @Test
    void testComplytFileMetadataToComplytFileMetadataDtoWithNull() {
        // Test that null input returns null output
        ComplytFileMetadataDto result = mapper.complytFileMetadataToComplytFileMetadataDto(null);
        assertNull(result, "Expected null when input is null");
    }

    @Test
    void testComplytFileMetadataDtoToComplytFileMetadataWithNull() {
        // Test that null input returns null output
        ComplytFileMetadata result = mapper.complytFileMetadataDtoToComplytFileMetadata(null);
        assertNull(result, "Expected null when input is null");
    }
}