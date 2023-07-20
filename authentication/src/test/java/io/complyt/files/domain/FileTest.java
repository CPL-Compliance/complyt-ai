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
class FileTest {
    private ApiKey file;

    @BeforeEach
    public void setUp() {
        file = TestUtilities.createFile();
    }

    @Test
    public void equals_IdenticalLinks_Equal() {
        // Given + When
        ApiKey referenceFile = TestUtilities.createFile(file.getComplytId(), file.getId());

        // Then
        assertEquals(file, referenceFile);
    }
}