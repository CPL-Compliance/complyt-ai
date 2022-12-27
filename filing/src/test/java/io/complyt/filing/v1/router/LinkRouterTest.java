package io.complyt.filing.v1.router;

import io.complyt.filing.domain.Link;
import io.complyt.filing.services.LinkService;
import io.complyt.filing.v1.handler.LinkHandler;
import io.complyt.filing.v1.mappers.LinkMapper;
import io.complyt.filing.v1.model.LinkDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {LinkRouter.class, LinkHandler.class, Jackson2JsonDecoder.class})
@WebFluxTest
public class LinkRouterTest {
    @Autowired
    private ApplicationContext context;

    @MockBean
    LinkService linkService;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void linkRoute_nullLinkHandler_ThrowsNullPointerException() {
//        // Given
//        LinkHandler nullLinkHandler = null;
//
//        // When
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> linkRouter.exemptionsRoute(nullLinkHandler));
//
//        // Then
//        assertEquals("linkHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @WithUserDetails
    void exemptionsRoute() {
        // Given

        Link link = new Link(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://localhost");
        LinkDto linkDto = LinkMapper.INSTANCE.linkToLinkDto(link);

        // When
        when(linkService.find()).thenReturn(Mono.just(link));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(LinkRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LinkDto.class)
                .isEqualTo(linkDto);
    }
}