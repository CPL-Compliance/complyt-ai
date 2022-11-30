package com.complyt.v1.controllers.router;

import com.complyt.v1.controllers.router.handler.ExemptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExemptionRouterTest {

    ExemptionRouter exemptionRouter;

    @Mock
    ExemptionHandler exemptionHandler;

    @BeforeEach
    void setup() {
        exemptionRouter = new ExemptionRouter();
    }

    @Test
    void exemptionRoute_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.exemptionsRoute(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void exemptionRoute_ExemptionHandler_RoutingToExemptionHandler() {
        // Given
        RouterFunction<ServerResponse> responseRouterFunction = exemptionRouter.exemptionsRoute(exemptionHandler);
        WebTestClient webTestClient = WebTestClient.bindToRouterFunction(responseRouterFunction).build();
        //ServerRequest.Builder buidler = DefaultServe

        // When
        when( exemptionHandler.getAll(Mockito.any(ServerRequest.class)))
                .thenReturn(ServerResponse.ok().body(Mono.just("hello"), String.class));

        // Then
        webTestClient.get()
                .uri(ExemptionRouter.BASE_URL + "")
                .accept(MediaType.APPLICATION_JSON)
                .exchange().expectStatus().isOk()
                .expectBody(String.class).isEqualTo("hello");
    }
}
