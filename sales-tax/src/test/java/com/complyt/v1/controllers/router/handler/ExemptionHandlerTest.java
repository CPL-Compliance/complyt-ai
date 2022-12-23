package com.complyt.v1.controllers.router.handler;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.*;
import com.complyt.facades.ExemptionFacade;
import com.complyt.v1.controllers.router.ExemptionRouter;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.model.customer.exemption.ExemptionDto;
import com.mongodb.client.result.DeleteResult;
import lombok.extern.slf4j.Slf4j;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ExemptionHandler.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
@Slf4j
@ContextConfiguration(classes = {ExemptionRouter.class, ExemptionHandler.class})
public class ExemptionHandlerTest {

    Exemption exemption;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ExemptionFacade exemptionFacade;

    @BeforeEach
    public void setUp() {
        exemption = createExemption();
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), (new ObjectId()).toString(), new ObjectId(),
                state, classification, validationDates, null, status, certificate, ExemptionType.FULLY);
    }

    @Test
    @WithUserDetails()
    public void getOne_FindsExemption_ReturnsExemption() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/" + exemption.getId();

        // When
        when(exemptionFacade.findById(exemption.getId())).thenReturn(Mono.just(exemption));
        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption);

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @WithUserDetails()
    public void getOne_ExemptionDoesNotExistInDB_Throws404NotFound() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/" + exemption.getId();

        // When
        when(exemptionFacade.findById(exemption.getId())).thenReturn(Mono.empty());

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @WithUserDetails()
    @Test
    public void create_CreatesExemption_ReturnsExemption() {
        // Given
        Exemption exemptionNoId = exemption.withId(null).withTenantId(null);
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemptionNoId);

        // When
        when(exemptionFacade.save(exemptionNoId)).thenReturn(Mono.just(exemption));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL).build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ExemptionDto.class)
                .isEqualTo(exemptionDto.withId(exemption.getId()));
    }

    @Test
    @WithUserDetails()
    void update_UpdatesExemption_ReturnsExemption() {
        // Given
        Exemption exemptionNoClientId = exemption.withTenantId(null);
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemptionNoClientId);

        // When
        when(exemptionFacade.update(exemptionNoClientId, exemption.getId())).thenReturn(Mono.just(exemption));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/" + exemption.getId())
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(exemptionDto.withId(exemption.getId()));
    }

    @Test
    @WithUserDetails()
    void update_ExemptionDoesNotExistInDB_Throws404NotFound() {
        // Given
        Exemption exemptionWithIdThatDoesNotExist = exemption.withTenantId(null).withId(UUID.randomUUID().toString());
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemptionWithIdThatDoesNotExist);

        // When
        when(exemptionFacade.update(exemptionWithIdThatDoesNotExist, exemption.getId())).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/" + exemption.getId())
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithUserDetails()
    void getAll_FindsTwoExemptions_ReturnsTwoExemptions() {
        // Given
        Exemption secondExemption = exemption.withId(UUID.randomUUID().toString())
                .withState(new State("NY", "05", "New York"));
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption);
        ExemptionDto secondExemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(secondExemption);

        List<Exemption> exemptions = new ArrayList<>() {{
            add(exemption);
            add(secondExemption);
        }};

        List<ExemptionDto> exemptionDtos = new ArrayList<>() {{
            add(exemptionDto);
            add(secondExemptionDto);
        }};

        // When
        when(exemptionFacade.findAll()).thenReturn(Flux.fromIterable(exemptions));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ExemptionDto.class)
                .isEqualTo(exemptionDtos);
    }

    @Test
    @WithUserDetails()
    public void delete_DeletesExemption_Returns204NoContent() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/" + exemption.getId();
        DeleteResult deleteResult = DeleteResult.acknowledged(1);

        // When
        when(exemptionFacade.delete(exemption.getId())).thenReturn(Mono.just(deleteResult));

        // Then
        webTestClient.mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @WithUserDetails()
    public void delete_ExemptionDoesNoExistInDB_Throws404NotFound() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/" + exemption.getId();

        // When
        when(exemptionFacade.delete(exemption.getId())).thenReturn(Mono.empty());

        // Then
        webTestClient.mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

}