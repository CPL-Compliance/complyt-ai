package com.complyt.v1.controllers.router.handler;

import com.complyt.domain.State;
import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.exemption.*;
import com.complyt.facades.ExemptionFacade;
import com.complyt.v1.controllers.router.ExemptionRouter;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ExemptionHandler.class)
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ExemptionRouter.class, ExemptionHandler.class})
@WithMockUser(username = "mock", password = "mock")
public class ExemptionHandlerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ExemptionFacade exemptionFacade;

    Exemption exemption;

    @BeforeEach
    public void setUp() {
        exemption = createExemption();
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), new ObjectId(), new ObjectId(),
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
    }

    @Test
    public void getOne_FindsExemption_ReturnsExemption() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/" + exemption.getId();

        // When
        Mockito.when(exemptionFacade.findById(exemption.getId())).thenReturn(Mono.just(exemption));

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .value(exemptionResponse -> {
                    Assertions.assertEquals(exemptionResponse.getId(), exemption.getId());
                });
    }
}
