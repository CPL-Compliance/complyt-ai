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
import org.springframework.security.test.context.support.WithMockUser;
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
public class ExemptionRouterTestImpl implements ExemptionRouterTest {

    ExemptionRouter exemptionRouter;
    @Autowired
    WebTestClient webTestClient;
    @MockBean
    ExemptionFacade exemptionFacade;
    Exemption exemption;
    ObjectStub objectStub;
    @MockBean
    private ValidationHandler<ExemptionDto, SpringValidatorAdapter> exemptionDtoValidationHandler;

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
    @WithMockUser
    public void getByComplytId_UnauthenticatedUser_Returns401() {

    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_UserWithoutAuthorities_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_InternalServerError_Returns500() {

    }

    @WithUserDetails()
    @Override
    @Test
    public void createByComplytId_Exists_Returns201() {
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
    @Override
    @WithMockUser
    public void createByComplytId_CoupleValidationsFailure_Returns400WithErrorList() {

    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_UnauthenticatedUser_Returns401() {

    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_UserWithoutAuthorities_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_UserWithoutCSRFToken_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void createByComplytId_InternalServerError_Returns500() {

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
    @Override
    @WithUserDetails()
    public void upsertByComplytId_DoesntExists_Returns404() {
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
    @Override
    @WithMockUser
    public void upsertByComplytId_CoupleValidationsFailure_Returns400WithErrorList() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_DifferentComplytIdInBody_Returns400ConflictedData() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_NullComplytId_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_BlankComplytId_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_ComplytIdFailedToParse_Returns400() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_UnauthenticatedUser_Returns401() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_UserWithoutAuthorities_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_UserWithoutCSRFToken_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsertByComplytId_InternalServerError_Returns500() {

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

    }

    @Test
    @Override
    @WithMockUser
    public void getAll_UnauthenticatedUser_Returns401() {

    }

    @Test
    @Override
    @WithMockUser
    public void getAll_UserWithoutAuthorities_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void getAll_InternalServerError_Returns500() {

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

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByComplytId_UnauthenticatedUser_Returns401() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByComplytId_UserWithoutAuthorities_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByComplytId_UserWithoutCSRFToken_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteByComplytId_InternalServerError_Returns500() {

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

    }

    @Test
    @Override
    @WithMockUser
    public void putAny_InvalidUrl_Returns404() {

    }

    @Test
    @Override
    @WithMockUser
    public void deleteAny_InvalidUrl_Returns404() {

    }

    @Test
    @Override
    @WithMockUser
    public void postAny_InvalidUrl_Returns404() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullClassification_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankCodeInClassification_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankDescriptionInClassification_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCodeInClassification_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullDescriptionInClassification_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CodeInClassification_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257DescriptionInClassification_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullStatus_Returns400validationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCodeInStatus_Returns400validationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullNameInStatus_Returns400validationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_blankCodeInStatus_Returns400validationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_blankNameInStatus_Returns400validationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257NameInStatus_Returns400validationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CodeInStatus_Returns400validationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCertificateIdInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullUrlInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullNameInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankCertificateIdInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankUrlInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankNameInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CertificateIdInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257UrlInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257NameInCertificate_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullExemptionType_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_InvalidTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_InvalidTimestampInCreatedDateInInternalTimestamp_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankAbbreviationInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankCodeInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankNameInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257AbbreviationInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257CodeInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthOf257NameInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullAbbreviationInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCodeInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullNameInState_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullValidationDates_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCreatedDateInValidationDates_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullUpdatedDateInValidationDates_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_InvalidTimestampInUpdatedDateInValidationDates_Returns400ValidationError() {

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_InvalidTimestampInCreatedDateInValidationDates_Returns400ValidationError() {

    }
}
