package com.complyt.v1.controllers.router.handler;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.State;
import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.exemption.*;
import com.complyt.facades.ExemptionFacade;
import com.complyt.v1.controllers.CustomerController;
import com.complyt.v1.controllers.TransactionController;
import com.complyt.v1.controllers.router.ExemptionRouter;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.equalTo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import java.time.LocalDateTime;
import java.util.UUID;

//@ExtendWith(SpringExtension.class)
//@ExtendWith(MockitoExtension.class)
//@WebFluxTest(ExemptionRouter.class)
public class ExemptionRouterTest {

    @Mock
    ExemptionFacade exemptionFacade;

    WebTestClient webTestClient;

    Exemption exemption;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ExemptionHandler exemptionHandler = new ExemptionHandler(exemptionFacade);
        ExemptionRouter exemptionRouter = new ExemptionRouter();
        RouterFunction<?> routerFunction = exemptionRouter.exemptionsRoute(exemptionHandler);
        exemption = createExemption();
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
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

    @WithUserDetails()
    @Test
    void getOne_FindsExemption_ReturnsExemption() {
        // Given
        String id = exemption.getId();
        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption);

        // When
        when(exemptionFacade.findById(id)).thenReturn(Mono.just(exemption));
        String url = ExemptionRouter.BASE_URL + "/" + id;

        // Then
        webTestClient.mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .value(e -> e, equalTo(expectedExemption));
    }

    @WithUserDetails()
    @Test
    void create_CreatesExemption_ReturnsExemption() {
        // Given
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption.withId(null));
        Exemption exemptionNoId = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);

        // When
        when(exemptionFacade.save(exemptionNoId)).thenReturn(Mono.just(exemption));

        // Then
        webTestClient.mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL)
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(ExemptionDto.class)
                .isEqualTo(exemptionDto);
    }
}
