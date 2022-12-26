package io.complyt.filing.domain;

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
class LinkTest {
    private Link link;

    @BeforeEach
    public void setUp(){
        link = new Link(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://localhost");
    }

    @Test
    public void equals_IdenticalLinks_Equal(){
        Link referenceLink = new Link(link.getId(), link.getTenantId(), link.getLink());

        assertEquals(link, referenceLink);
    }
}