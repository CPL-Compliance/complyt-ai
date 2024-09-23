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
class ComplytFileTest {
    private ComplytFile complytFile;

    @BeforeEach
    public void setUp() {
        complytFile = TestUtilities.createComplytFile();
    }


    @Test
    public void equals_SameFile_ReturnsTrue() {
        // Given
        ComplytFile referenceComplytFile = TestUtilities.createComplytFile(complytFile.getFile(), complytFile.getMetadata());
        // Then
        assertEquals(complytFile, referenceComplytFile);
    }
}
