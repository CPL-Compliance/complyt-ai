package io.complyt.filing.v1.mappers;

import io.complyt.filing.domain.Link;
import io.complyt.filing.v1.model.LinkDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class LinkMapperTest {
    private Link link;
    private LinkDto linkDto;

    @BeforeEach
    void setUp() {
        String linkStr = "http:localhost";
        link = new Link(ObjectId.get().toString(), UUID.randomUUID().toString(), linkStr);
        linkDto = new LinkDto(linkStr);
    }

    @Test
    void linkToLinkDto_Link_returnLinkDto() {
        // Given
        Link givenLink = link;

        // When
        LinkDto actualLinkDto = LinkMapper.INSTANCE.linkToLinkDto(givenLink);

        // Then
        assertEquals(linkDto, actualLinkDto);
    }
}