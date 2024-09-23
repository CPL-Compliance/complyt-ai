package io.complyt.files.v1.mappers;

import io.complyt.files.domain.ComplytFile;
import io.complyt.files.domain.ComplytFileMetadata;
import io.complyt.files.v1.models.ComplytFileDto;
import io.complyt.files.v1.models.ComplytFileMetadataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ComplytFileMapperTest {
    private ComplytFile complytFile;
    private ComplytFileMetadata complytFileMetadata;
    private ComplytFileDto complytFileDto;
    private ComplytFileMetadataDto complytFileMetadataDto;

    @BeforeEach
    void setUp() {
        complytFile = TestUtilities.createComplytFile();
        complytFileMetadata = complytFile.getMetadata();
        complytFileDto = TestUtilities.createComplytFileDto(
                complytFile.getFile(),
                TestUtilities.createComplytFileMetadataDto(
                        complytFile.getMetadata().complytId(),
                        complytFile.getMetadata().metadata(),
                        complytFile.getMetadata().tenantId(),
                        complytFile.getMetadata().updateTime(),
                        complytFile.getMetadata().createTime(),
                        complytFile.getMetadata().link()
                )
        );
        complytFileMetadataDto = complytFileDto.metadata();
    }

    @Test
    void complytFileToComplytFileDto_ComplytFile_ReturnComplytFileDto() {
        // When
        ComplytFileDto actualComplytFileDto = ComplytFileMapper.INSTANCE.complytFileToComplytFileDto(complytFile);

        // Then
        assertEquals(complytFileDto, actualComplytFileDto);
    }

    @Test
    void complytFileDtoToComplytFile_ComplytFileDto_ReturnComplytFile() {
        // When
        ComplytFile actualComplytFile = ComplytFileMapper.INSTANCE.complytFileDtoToComplytFile(complytFileDto);

        //Then
        assertEquals(complytFile, actualComplytFile);
    }
}
