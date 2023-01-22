package io.complyt.files.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.ObjectStub;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FileTest {
    private File file;
    ObjectStub objectStub = new ObjectStub();

    @BeforeEach
    public void setUp(){
        file = objectStub.createFile();
    }

    @Test
    public void equals_IdenticalLinks_Equal(){
        // Given + When
        File referenceFile = objectStub.createFile(file.getComplytId(),file.getId());

        // Then
        assertEquals(file, referenceFile);
    }
}