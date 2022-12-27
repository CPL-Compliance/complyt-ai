package io.complyt.filing.v1.model;

import io.complyt.filing.domain.Link;
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
class LinkDtoTest {
    private LinkDto linkDto;

    @BeforeEach
    public void setUp() {
        linkDto = new LinkDto("http://localhost");
    }

    @Test
    public void equals_IdenticalLinks_Equal() {
        LinkDto referenceLinkDto = new LinkDto(linkDto.getLink());

        assertEquals(linkDto, referenceLinkDto);
    }
}