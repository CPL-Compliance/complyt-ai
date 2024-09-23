package io.complyt.files.v1.models;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ComplytFileDtoTest {
    private ComplytFileDto complytFileDto;

    @BeforeEach
    public void setUp() {
        complytFileDto = TestUtilities.createComplytFileDto();
    }

    @Test
    public void equals_SameFile_ReturnsTrue() {
        // Given
        ComplytFileDto referenceComplytFileDto = TestUtilities.createComplytFileDto(complytFileDto.file(), complytFileDto.metadata());
        // Then
        assertEquals(complytFileDto, referenceComplytFileDto);
    }
}
