package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.config.JacksonConfig;
import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.ExemptionFacade;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handlers.ExemptionHandler;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.validators.ValidationHandler;
import com.complyt.v1.validators.ValidatorConfig;
import com.mongodb.client.result.DeleteResult;
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
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ExemptionHandler.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
@ContextConfiguration(classes = {ExemptionRouter.class, ExemptionHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class ExemptionRouterTest {

    ExemptionRouter exemptionRouter;

    @MockBean
    private ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    ExemptionFacade exemptionFacade;

    Exemption exemption;

    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        exemptionRouter = new ExemptionRouter();
        exemption = objectStub.createExemption(UUID.randomUUID().toString())
                .withInternalTimestamps(null)
                .withValidationDates(null);
    }

    @Test
    @WithUserDetails()
    public void getOne_FindsExemption_ReturnsExemption() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId();

        // When
        when(exemptionFacade.findByComplytId(exemption.getComplytId())).thenReturn(Mono.just(exemption));
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
        String url = ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId();

        // When
        when(exemptionFacade.findByComplytId(exemption.getComplytId())).thenReturn(Mono.empty());

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
        Exemption exemptionNoId = exemption.withId(null).withTenantId(null).withComplytId(null);
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemptionNoId);

        // When
        when(exemptionDtoValidationHandler.validate(any())).thenReturn(Mono.just(exemptionDto));
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
                .isEqualTo(exemptionDto.withComplytId(exemption.getComplytId()));
    }

    @Test
    @WithUserDetails()
    void update_UpdatesExemption_ReturnsExemption() {
        // Given
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption);
        Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);

        // When
        when(exemptionFacade.update(receivedExemption, exemption.getComplytId())).thenReturn(Mono.just(exemption));
        when(exemptionDtoValidationHandler.validate(any())).thenReturn(Mono.just(exemptionDto));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId().toString())
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(exemptionDto);
    }

    @Test
    @WithUserDetails()
    void update_ExemptionDoesNotExist_Throws404NotFound() {
        // Given
        Exemption exemptionWithIdThatDoesNotExist = exemption.withTenantId(null).withId(UUID.randomUUID().toString());
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemptionWithIdThatDoesNotExist);

        // When
        when(exemptionFacade.update(exemptionWithIdThatDoesNotExist, exemption.getComplytId())).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/complytId" + exemption.getId())
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
        UUID complytId = UUID.randomUUID();
        String url = ExemptionRouter.BASE_URL + "/complytId/" + complytId;
        DeleteResult deleteResult = DeleteResult.acknowledged(1);

        // When
        when(exemptionFacade.delete(complytId)).thenReturn(Mono.just(deleteResult));

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
        UUID complytId = UUID.randomUUID();
        String url = ExemptionRouter.BASE_URL + "/complytId/" + complytId;

        // When
        when(exemptionFacade.delete(complytId)).thenReturn(Mono.empty());

        // Then
        webTestClient.mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void GetExemptionByComplytIdRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.GetExemptionByComplytIdRouterFunction(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void GetAllExemptionsRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.GetAllExemptionsRouterFunction(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void postExemptionRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.postExemptionRouterFunction(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void updateExemptionByComplytIdRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.updateExemptionByComplytIdRouterFunction(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void deleteExemptionByComplytIdRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.deleteExemptionByComplytIdRouterFunction(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }


}
