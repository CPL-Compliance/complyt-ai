package com.complyt.v1.routers;

import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.facades.ExemptionFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handlers.ExemptionHandler;
import com.complyt.v1.mappers.ExemptionMapper;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.*;
import com.complyt.v1.models.timestamps.TimestampsDto;
import com.mongodb.client.result.DeleteResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.ut.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {ExemptionRouter.class, ExemptionHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class ExemptionRouterTest implements ExemptionRouterTestTemplate {

    ExemptionRouter exemptionRouter;
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    ExemptionFacade exemptionFacade;
    Exemption exemption;
    ExemptionDto exemptionDto;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        exemptionRouter = new ExemptionRouter();
        exemptionDto = testUtilities.createExemptionDto();
        exemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);
    }

    @Test
    @Override
    @WithUserDetails()
    public void getByComplytId_Exists_Returns200() {
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
    @Override
    @WithUserDetails()
    public void getByComplytId_DoesntExists_Returns404() {
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

    @Test
    @Override
    public void getByComplytId_UnauthenticatedUser_Returns401() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId();

        // When + Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_InternalServerError_Returns500() {
        // Given
        String url = ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId();

        // When
        when(exemptionFacade.findByComplytId(exemption.getComplytId())).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @WithUserDetails()
    @Override
    @Test
    public void createByComplytId_Valid_Returns201() {
        // Given
        Exemption exemptionNoId = exemption.withId(null).withTenantId(null).withComplytId(null);
        ExemptionDto requestExemptionDto = exemptionDto.withComplytId(null);

        // When
        when(exemptionFacade.save(exemptionNoId)).thenReturn(Mono.just(exemption));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL).build())
                .bodyValue(requestExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ExemptionDto.class)
                .isEqualTo(requestExemptionDto.withComplytId(exemption.getComplytId()));
    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_CoupleValidationsFailure_Returns400WithErrorList() {
        // Given
        ExemptionDto givenExemptionDto = exemptionDto
                .withStatus(null)
                .withState(new StateDto("Ma", "09", ""))
                .withCertificate(new CertificateDto("id", null, "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR,
                "Certificate.url " + DtoErrorMessages.NOT_NULL_ERROR,
                "status " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    public void createByComplytId_UnauthenticatedUser_Returns401() {
        // Given
        ExemptionDto requestExemptionDto = exemptionDto.withComplytId(null);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL).build())
                .bodyValue(requestExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_UserWithoutCSRFToken_Returns403() {
        // Given
        ExemptionDto requestExemptionDto = exemptionDto.withComplytId(null);

        // When + Then
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL).build())
                .bodyValue(requestExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_InternalServerError_Returns500() {
        // Given
        Exemption exemptionNoId = exemption.withId(null).withTenantId(null).withComplytId(null);
        ExemptionDto requestExemptionDto = exemptionDto.withComplytId(null);

        // When
        when(exemptionFacade.save(exemptionNoId)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL).build())
                .bodyValue(requestExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithUserDetails()
    public void upsertByComplytId_Exists_Returns200() {
        // Given
        ExemptionDto exemptionDto = ExemptionMapper.INSTANCE.exemptionToExemptionDto(exemption);
        Exemption receivedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(exemptionDto);

        // When
        when(exemptionFacade.update(receivedExemption, exemption.getComplytId())).thenReturn(Mono.just(exemption));

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
    @Override
    @WithUserDetails()
    public void upsertByComplytId_DoesntExists_Returns404() {
        // Given + When
        when(exemptionFacade.update(exemption, exemption.getComplytId())).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId())
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_CoupleValidationsFailure_Returns400WithErrorList() {
        // Given
        ExemptionDto givenExemptionDto = exemptionDto
                .withStatus(null)
                .withState(new StateDto("Ma", "09", ""))
                .withCertificate(new CertificateDto("id", null, "name"));
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR,
                "Certificate.url " + DtoErrorMessages.NOT_NULL_ERROR,
                "status " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_DifferentComplytIdInBody_Returns400ConflictedData() {
        // Given
        UUID complytId = exemptionDto.complytId();
        UUID differentComplytId = UUID.randomUUID();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exemptionDto.withComplytId(differentComplytId)
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals(GenericErrorMessages.DATA_CONFLICT_ERROR, map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_NullComplytId_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exemptionDto.withComplytId(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals(GenericErrorMessages.DATA_CONFLICT_ERROR, map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_BlankComplytId_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals(GenericErrorMessages.DATA_CONFLICT_ERROR, map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_ComplytIdFailedToParse_Returns400() {
        // Given
        UUID complytId = exemptionDto.complytId();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"24105509-ff95-4408-b058-3eead7ae6fd7Q\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\"\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("Failed to read HTTP message", map.get("message"));
                });
    }

    @Test
    @Override
    public void upsertByComplytId_UnauthenticatedUser_Returns401() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId().toString())
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_UserWithoutCSRFToken_Returns403() {
        // Given + When + Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/complytId/" + exemption.getComplytId().toString())
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_InternalServerError_Returns500() {
        // Given
        UUID complytId = exemptionDto.complytId();

        // When
        when(exemptionFacade.findByComplytId(complytId)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .bodyValue(exemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithUserDetails()
    public void getAll_Exists_Returns200WithList() {
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
    @Override
    @WithMockUser
    public void getAll_EmptyCollection_Returns200WithEmptyList() {
        // Given
        List<ExemptionDto> exemptionDtos = new ArrayList<>();

        // When
        when(exemptionFacade.findAll()).thenReturn(Flux.empty());

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
    @Override
    public void getAll_UnauthenticatedUser_Returns401() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_UserWithoutAuthorities_Returns403() {
        // ??
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_InternalServerError_Returns500() {
        // Given + When
        when(exemptionFacade.findAll()).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithUserDetails()
    public void deleteByComplytId_Exists_Returns204() {
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
    @Override
    @WithMockUser
    public void deleteByComplytId_DoesntExists_Returns404() {
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
    @Override
    public void deleteByComplytId_UnauthenticatedUser_Returns401() {
        // Given
        UUID complytId = UUID.randomUUID();
        String url = ExemptionRouter.BASE_URL + "/complytId/" + complytId;

        // When + Then
        webTestClient.mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void deleteByComplytId_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void deleteByComplytId_UserWithoutCSRFToken_Returns403() {
        // Given
        UUID complytId = UUID.randomUUID();
        String url = ExemptionRouter.BASE_URL + "/complytId/" + complytId;

        // When + Then
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Override
    @WithMockUser
    public void deleteByComplytId_InternalServerError_Returns500() {
        // Given
        UUID complytId = UUID.randomUUID();
        String url = ExemptionRouter.BASE_URL + "/complytId/" + complytId;

        // When
        when(exemptionFacade.findByComplytId(complytId)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder.path(url).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
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
    @Override
    @WithMockUser
    public void getByComplytId_NullHandler_ThrowsNullPointerException() {
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
    @Override
    @WithMockUser
    public void getAll_NullHandler_ThrowsNullPointerException() {
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
    @Override
    @WithMockUser
    public void createByComplytId_NullHandler_ThrowsNullPointerException() {
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
    @Override
    @WithMockUser
    public void upsertByComplytId_NullHandler_ThrowsNullPointerException() {
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
    @Override
    @WithMockUser
    public void deleteByComplytId_NullHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.deleteExemptionByComplytIdRouterFunction(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }


    @Test
    @Override
    @WithMockUser
    public void getAny_InvalidUrl_Returns404() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "wrong/url").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void putAny_InvalidUrl_Returns404() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "wrong/url").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void deleteAny_InvalidUrl_Returns404() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "wrong/url").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void postAny_InvalidUrl_Returns404() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(ExemptionRouter.BASE_URL + "wrong/url").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullClassification_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exemptionDto.withClassification(null)
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[classification may not be null]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankCodeInClassification_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withClassification(new ClassificationDto("", "desc"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Classification.code " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankDescriptionInClassification_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withClassification(new ClassificationDto("code", ""));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Classification.description " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCodeInClassification_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withClassification(new ClassificationDto(null, "desc"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Classification.code " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullDescriptionInClassification_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withClassification(new ClassificationDto("code", null));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Classification.description " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CodeInClassification_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withClassification(new ClassificationDto("baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1", "desc"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Classification.code " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257DescriptionInClassification_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withClassification(new ClassificationDto("code", "baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Classification.description " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullStatus_Returns400validationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withStatus(null);
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "status " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCodeInStatus_Returns400validationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withStatus(new StatusDto(null, "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Status.code " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullNameInStatus_Returns400validationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withStatus(new StatusDto("code", null));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Status.name " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_blankCodeInStatus_Returns400validationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withStatus(new StatusDto("", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Status.code " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_blankNameInStatus_Returns400validationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withStatus(new StatusDto("code", ""));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Status.name " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257NameInStatus_Returns400validationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withStatus(new StatusDto("code", "baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Status.name " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CodeInStatus_Returns400validationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withStatus(new StatusDto("baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Status.code " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(null);
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "certificate " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCertificateIdInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto(null, "url", "name"));

        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.id " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullUrlInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("id", null, "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.url " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullNameInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("id", "url", null));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.name " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankCertificateIdInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("", "url", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.id " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankUrlInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("id", "", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.url " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankNameInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("id", "url", ""));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.name " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CertificateIdInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1", "url", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.id " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257UrlInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("id", "baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.url " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257NameInCertificate_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withCertificate(new CertificateDto("id", "url", "baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Certificate.name " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullExemptionType_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "exemptionType " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exemptionDto.withExemptionType(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "validationDates " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.NOT_NULL_ERROR,
                "Timestamps.createdDate " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.NOT_NULL_ERROR,
                "Timestamps.updatedDate " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInCreatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"createdDate\": \"\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInCreatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-29T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-29T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                "2023-03-27T03:40:59+09:50",
                exemptionDto.internalTimestamps().updatedDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                exemptionDto.internalTimestamps().createdDate(),
                "2023-03-27T03:40:59.999999999"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInCreatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00.9999999999\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00.9999999999\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                "2023-03-27T03:40:59Z",
                exemptionDto.internalTimestamps().updatedDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                exemptionDto.internalTimestamps().createdDate(),
                "2023-03-27T03:40:59Z"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                "2023-03-27T03:40:59+09:50",
                exemptionDto.internalTimestamps().updatedDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                exemptionDto.internalTimestamps().createdDate(),
                "2023-03-27T03:40:59+09:50"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                "2023-03-27T03:40:59-18:00",
                exemptionDto.internalTimestamps().updatedDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                exemptionDto.internalTimestamps().updatedDate(),
                "2023-03-27T03:40:59-18:00"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InCreatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00+18:01\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InUpdatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00+18:01\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_JustDateWithNoTimeOffsetInUpdatedDateInInternalTimestamps_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withInternalTimestamps(new TimestampsDto(
                exemptionDto.internalTimestamps().createdDate(),
                "2023-03-27"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);

    }

    @Override
    public void upsert_JustDateWithNoTimeOffsetInCreatedDateInInternalTimestamps_Returns200Ok() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "state " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(exemptionDto.withState(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankAbbreviationInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("", "code", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.abbreviation " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankCodeInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("CA", "", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.code " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankNameInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("CA", "code", ""));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257AbbreviationInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1", "code", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.abbreviation " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CodeInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("CA", "baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.code " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257NameInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("CA", "code", "baabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab1"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullAbbreviationInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto(null, "code", "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.abbreviation " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCodeInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("CA", null, "name"));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.code " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullNameInState_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        ExemptionDto givenExemptionDto = exemptionDto.withState(new StateDto("CA", "code", null));
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "State.name " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullFromDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.fromDate " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"validationDates\": {\n" +
                        "        \"toDate\": \"2023-02-28T02:00:00\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\"\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullToDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.toDate " + DtoErrorMessages.NOT_NULL_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"validationDates\": {\n" +
                        "        \"fromDate\": \"2023-02-28T02:00:00\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\"\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInToDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.toDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"validationDates\": {\n" +
                        "        \"fromDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"toDate\": \"\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\"\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInFromDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.fromDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"validationDates\": {\n" +
                        "        \"fromDate\": \"\",\n" +
                        "        \"toDate\": \"2023-02-28T02:00:00\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\"\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInFromDateInValidationDates_Returns400ValidationError() {
// Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.fromDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2023-02-29T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInToDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.toDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2023-02-29T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInFromDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                "2023-03-27T03:40:59.999999999",
                exemptionDto.validationDates().toDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInToDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                exemptionDto.validationDates().fromDate(),
                "2023-03-27T03:40:59.999999999"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInFromDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.fromDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"validationDates\": {\n" +
                        "        \"fromDate\": \"2023-02-28T02:00:00.0000000000\",\n" +
                        "        \"toDate\": \"2023-02-28T02:00:00\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\"\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInToDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.toDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"validationDates\": {\n" +
                        "        \"fromDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"toDate\": \"2023-02-28T02:00:00.0000000000\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\"\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInFromDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                "2023-03-27T03:40:59Z",
                exemptionDto.validationDates().toDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInToDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                exemptionDto.validationDates().fromDate(),
                "2023-03-27T03:40:59Z"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInFromDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                "2023-03-27T03:40:59+09:50",
                exemptionDto.validationDates().toDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInToDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                exemptionDto.validationDates().fromDate(),
                "2023-03-27T03:40:59+09:50"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInFromDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                "2023-03-27T03:40:59.-18:00", exemptionDto.validationDates().toDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInToDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                exemptionDto.validationDates().fromDate(), "2023-03-27T03:40:59.-18:00"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InFromDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.fromDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00+18:01\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InToDateInValidationDates_Returns400ValidationError() {
        // Given
        UUID complytId = exemptionDto.complytId();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "ValidationDates.toDate " + DtoErrorMessages.DATE_FORMAT_ERROR));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                        "    \"complytId\": \"" + complytId + "\",\n" +
                        "    \"customerId\": \"24105509-ff95-4408-b058-3eead7ae6fd7\",\n" +
                        "    \"state\": {\n" +
                        "        \"abbreviation\": \"CA\",\n" +
                        "        \"code\": \"02\",\n" +
                        "        \"name\": \"California\"\n" +
                        "    },\n" +
                        "    \"classification\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"description\": \"description\"\n" +
                        "    },\n" +
                        "    \"status\": {\n" +
                        "        \"code\": \"code\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"certificate\": {\n" +
                        "        \"certificateId\": \"id\",\n" +
                        "        \"url\": \"url\",\n" +
                        "        \"name\": \"name\"\n" +
                        "    },\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"exemptionType\": \"FULLY\",\n" +
                        "    \"validationDates\": {\n" +
                        "       \"fromDate\": \"2022-11-01T02:00:00\",\n" +
                        "       \"toDate\": \"2022-11-01T02:00:00+18:01\"\n" +
                        "    },\n" +
                        "    \"internalTimestamps\": {\n" +
                        "        \"createdDate\": \"2023-02-28T02:00:00\",\n" +
                        "        \"updatedDate\": \"2023-02-28T02:00:00\"\n" +
                        "    }\n" +
                        "}"
                )
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_JustDateWithNoTimeOffsetToDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                exemptionDto.validationDates().fromDate(),
                "2023-03-27"
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_JustDateWithNoTimeOffsetFromDateInValidationDates_Returns200Ok() {
        // Given
        UUID complytId = exemptionDto.complytId();

        ExemptionDto givenExemptionDto = exemptionDto.withValidationDates(new ValidationDatesDto(
                "2023-03-27",
                exemptionDto.validationDates().toDate()
        ));
        Exemption recievedExemption = ExemptionMapper.INSTANCE.exemptionDtoToExemption(givenExemptionDto);

        ExemptionDto expectedExemption = ExemptionMapper.INSTANCE.exemptionToExemptionDto(recievedExemption);

        // When
        when(exemptionFacade.update(recievedExemption, exemption.getComplytId())).thenReturn(Mono.just(recievedExemption));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ExemptionRouter.BASE_URL + "/complytId/" + complytId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenExemptionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ExemptionDto.class)
                .isEqualTo(expectedExemption);
    }
}
