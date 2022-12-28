package io.complyt.files.domain;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class FileTest {
    private File file;

    @BeforeEach
    public void setUp(){
        file = new File(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://localhost");
    }

    @Test
    public void equals_IdenticalLinks_Equal(){
        File referenceFile = new File(file.getId(), file.getTenantId(), file.getLink());

        assertEquals(file, referenceFile);
    }
}