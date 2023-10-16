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
class FileDtoTest {
    private FileDto fileDto;
    private String linkStr = "http://localhost";

    @BeforeEach
    public void setUp() {

        fileDto = TestUtilities.createFileDto();
    }

    @Test
    public void equals_IdenticalLinks_Equal() {
        FileDto referenceFileDto = TestUtilities.createFileDto(fileDto.complytId());

        assertEquals(fileDto, referenceFileDto);
    }
}