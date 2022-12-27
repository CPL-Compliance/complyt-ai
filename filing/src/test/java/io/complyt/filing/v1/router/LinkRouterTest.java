//package io.complyt.filing.v1.router;
//
//import io.complyt.filing.domain.Link;
//import io.complyt.filing.v1.handler.LinkHandler;
//import io.complyt.filing.v1.mappers.LinkMapper;
//import io.complyt.filing.v1.model.LinkDto;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithUserDetails;
//import org.springframework.test.web.reactive.server.WebTestClient;
//import org.springframework.web.reactive.function.server.ServerResponse;
//
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//
//@SpringBootTest(classes = {LinkRouter.class})
//public class LinkRouterTest {
//    @Autowired
//    LinkRouter linkRouter;
//
//    @MockBean
//    LinkHandler linkHandler;
//
//
////    @Test
////    void linkRoute_nullLinkHandler_ThrowsNullPointerException() {
////        // Given
////        LinkHandler nullLinkHandler = null;
////
////        // When
////        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> linkRouter.exemptionsRoute(nullLinkHandler));
////
////        // Then
////        assertEquals("linkHandler is marked non-null but is null", nullPointerException.getMessage());
////    }
//
//    @Test
//    @WithUserDetails
//    void exemptionsRoute() {
//        // Given
//        WebTestClient webTestClient = WebTestClient.bindToRouterFunction(linkRouter.exemptionsRoute()).build();
//
//        Link link = new Link(ObjectId.get().toString(), UUID.randomUUID().toString(), "http://localhost");
//        LinkDto linkDto = LinkMapper.INSTANCE.linkToLinkDto(link);
//
//        // When
//        given(linkHandler.getAll(any())).willReturn(ServerResponse.ok().bodyValue(linkDto));
//
//        // Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder.path(LinkRouter.BASE_URL).build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(LinkDto.class)
//                .isEqualTo(linkDto);
//    }
//}