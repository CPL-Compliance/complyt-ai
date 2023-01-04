package com.complyt.v1.controllers.routers;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.*;
import com.complyt.facades.ExemptionFacade;
import com.complyt.v1.controllers.handlers.ExemptionHandler;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ExemptionHandler.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
@ContextConfiguration(classes = {ExemptionRouter.class, ExemptionHandler.class})
public class ExemptionRouterTest {

    ExemptionRouter exemptionRouter;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ExemptionFacade exemptionFacade;

    String exemptionId;

    @BeforeEach
    void setup() {
        exemptionId = UUID.randomUUID().toString();
        exemptionRouter = new ExemptionRouter();
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(exemptionId, UUID.randomUUID().toString(), new ObjectId(),
                state, classification, validationDates, null, status, certificate, ExemptionType.FULLY);
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
    @WithUserDetails
    void exemptionRoute_ExemptionHandler_RoutingToExemptionHandler() {
        // Given
        Exemption expectedExemption = createExemption();
        ExemptionDto expectedExemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(expectedExemption);

        // When
        when(exemptionFacade.findById(exemptionId)).thenReturn(Mono.just(expectedExemption));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/" + exemptionId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemptionDto);
    }

}
