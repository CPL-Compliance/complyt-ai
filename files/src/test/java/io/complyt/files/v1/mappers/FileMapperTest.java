package io.complyt.files.v1.mappers;

import io.complyt.files.domain.File;
import io.complyt.files.v1.models.FileDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.ObjectStub;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FileMapperTest {
    private File file;
    private FileDto fileDto;

    private ObjectStub objectStub = new ObjectStub();

    @BeforeEach
    void setUp() {
        String linkStr = "http:localhost";
        file = objectStub.createFile();
        fileDto = objectStub.createFileDto(file.getComplytId());
    }

    @Test
    void linkToLinkDto_Link_returnLinkDto() {

        // When
        FileDto actualFileDto = FileMapper.INSTANCE.fileToFileDto(file);

        // Then
        assertEquals(fileDto, actualFileDto);
    }

    @Test
    void linkToLinkDto_LinkIsNull_returnNull() {

        // When
        FileDto actualFileDto = FileMapper.INSTANCE.fileToFileDto(null);

        // Then
        assertEquals(null, actualFileDto);
    }
}