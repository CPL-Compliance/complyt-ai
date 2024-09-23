package io.complyt.files.v1.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComplytFileMetadataDtoTest {
    private ComplytFileMetadataDto complytFileMetadataDto;

    @BeforeEach
    public void setUp() {
        complytFileMetadataDto = TestUtilities.createComplytFileMetadataDto();
    }

    @Test
    public void equals_SameFileMetadataDto_ReturnsTrue() {
        // Given
        ComplytFileMetadataDto referanceComplytFileMetadataDto = TestUtilities.createComplytFileMetadataDto(
                complytFileMetadataDto.complytId(),
                complytFileMetadataDto.metadata(),
                complytFileMetadataDto.tenantId(),
                complytFileMetadataDto.updateTime(),
                complytFileMetadataDto.createTime(),
                complytFileMetadataDto.link());
        // Then
        assertEquals(complytFileMetadataDto, referanceComplytFileMetadataDto);
    }

    @Test
    public void toString_ReturnsString() {
        // Given
        String expectedString = "ComplytFileMetadataDto[complytId=" + complytFileMetadataDto.complytId().toString() +
                ", metadata=" + complytFileMetadataDto.metadata().toString() +
                ", tenantId=" + complytFileMetadataDto.tenantId() +
                ", updateTime=" + complytFileMetadataDto.updateTime() +
                ", createTime=" + complytFileMetadataDto.createTime() +
                ", link=" + complytFileMetadataDto.link() + "]";

        // When
        String actualString = complytFileMetadataDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
