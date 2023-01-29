//package io.complyt.filing.v1.handler;
//
//import io.complyt.filing.domain.Link;
//import io.complyt.filing.services.LinkService;
//import io.complyt.filing.v1.mappers.LinkMapper;
//import io.complyt.filing.v1.models.LinkDto;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.reactive.function.server.MockServerRequest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.util.UUID;
//
//import static org.mockito.Mockito.when;
//
//@ExtendWith(SpringExtension.class)
//@ExtendWith(MockitoExtension.class)
//class LinkHandlerTest {
//    @InjectMocks
//    LinkHandler linkHandler;
//
//    @Mock
//    LinkService linkService;
//
//    @Test
//    void getAll() {
//        Link link = new Link(ObjectId.get().toString(), UUID.randomUUID().toString(), "http:localhost");
//        LinkDto linkDto = LinkMapper.INSTANCE.linkToLinkDto(link);
//        Mono<ServerResponse> serverResponseMonoEx = ServerResponse.ok().bodyValue(linkDto);
//        when(linkService.find()).thenReturn(Mono.just(link));
//        MockServerRequest mockServerRequest = MockServerRequest.builder().build();
//        Mono<ServerResponse> serverResponseMono = linkHandler.getAll(mockServerRequest);
//        StepVerifier.create(serverResponseMono).expectNext(serverResponseMonoEx.block()).verifyComplete();
//    }
//}