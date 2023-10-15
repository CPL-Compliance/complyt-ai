package com.complyt.v1.routers;

import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.handlers.SalesTaxTrackingHandler;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.EconomicNexusTrackerDto;
import com.complyt.v1.models.PhysicalNexusTrackerDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {SalesTaxTrackingRouter.class, SalesTaxTrackingHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class SalesTaxTrackingRouterTest implements SalesTaxTrackingRouterTestTemplate {

    SalesTaxTrackingRouter salesTaxTrackingRouter;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    SalesTaxTracking salesTaxTracking;

    SalesTaxTrackingDto salesTaxTrackingDto;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        salesTaxTrackingRouter = new SalesTaxTrackingRouter();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTrackingDto = testUtilities.createSalesTaxTrackingDto();
        salesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_Exists_Returns200WithList() {

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        String state = expectedSalesTaxTrackingDto.state().name();

        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.just(salesTaxTracking));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);

    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_Exists_Returns200() {

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        UUID complytId = expectedSalesTaxTrackingDto.complytId();

        when(salesTaxTrackingFacade.findByComplytId(complytId)).thenReturn(Mono.just(salesTaxTracking));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + complytId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_DoesntExists_Returns404() {

        String state = salesTaxTracking.getState().getName();

        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.empty());

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    public void getByState_UnauthenticatedUser_Returns401() {
        String state = salesTaxTrackingDto.state().name();

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_NoBody_Returns400() {
        // Given
        String state = "CA";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_InternalServerError_Returns500() {
        String state = salesTaxTrackingDto.state().name();

        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.error(new OperationFailedException()));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_DoesntExists_Returns404() {

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        UUID complytId = expectedSalesTaxTrackingDto.complytId();

        when(salesTaxTrackingFacade.findByComplytId(complytId)).thenReturn(Mono.empty());

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + complytId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

    }

    @Test
    @Override
    public void getByComplytId_UnauthenticatedUser_Returns401() {
        UUID complytId = salesTaxTrackingDto.complytId();

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + complytId).build())
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
        UUID complytId = salesTaxTrackingDto.complytId();

        when(salesTaxTrackingFacade.findByComplytId(complytId)).thenReturn(Mono.error(new OperationFailedException()));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + complytId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_DoesntExists_Returns201() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null);
        String state = newSalesTaxTracking.getState().getName();
        SalesTaxTracking salesTaxTrackingWithId = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state).build())
                .bodyValue(salesTaxTrackingDtoSent)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_CoupleValidationsFailure_Returns400WithErrorList() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "", "name"))
                .withEconomicNexusTracker(new EconomicNexusTrackerDto(true, null));
        Set<String> expectedErrors = Set.of(
                "State.code " + StringErrorMessages.MINMAX_256_ERROR,
                "EconomicNexusTracker.establishedDate " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_DifferentStateInBody_Returns400ConflictedData() {
        // Given
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", salesTaxTrackingDto.state().name() + "boo"));
        String stateName = salesTaxTrackingDto.state().name();
        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map,
                        Set.of("state " + DtoErrorMessages.STATE_CONFLICTED_WITH_URL_ERROR)));
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_ExistWithDifferentComplytId_Returns400ConflictedData() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        UUID differentComplytId = UUID.randomUUID();
        SalesTaxTracking differentSalesTaxTracking = salesTaxTracking.withComplytId(differentComplytId);

        // When
        when(salesTaxTrackingFacade.findByState(stateName)).thenReturn(Mono.just(differentSalesTaxTracking));
        when(salesTaxTrackingFacade.update(salesTaxTracking, differentSalesTaxTracking)).thenReturn(Mono.error(new ConflictedDataApiException()));
        when(salesTaxTrackingFacade.save(salesTaxTracking)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
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
    public void upsertByState_DoesntExistAndHasComplytId_Returns400ConflictedData() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.findByState(stateName)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(salesTaxTracking)).thenReturn(Mono.error(new ConflictedDataApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
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
    public void upsertByState_ComplytIdFailedToParse_Returns400() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n" +
                           "    \"approved\": \"true\",\n" +
                           "    \"complytId\": \"1111-boohoo\",\n" +
                           "    \"enforcesSalesTax\": \"true\",\n" +
                           "    \"state\": {\n" +
                           "        \"abbreviation\": \"CA\",\n" +
                           "        \"code\": \"02\",\n" +
                           "        \"name\": \"\"\n" +
                           "    },\n" +
                           "    \"physicalNexusTracker\": {\n" +
                           "        \"established\": \"true\",\n" +
                           "        \"establishedDate\": \"2023-02-28T02:00:00\"\n" +
                           "    },\n" +
                           "    \"economicNexusTracker\": {\n" +
                           "        \"established\": \"true\",\n" +
                           "        \"establishedDate\": \"2023-02-28T02:00:00\"\n" +
                           "    },\n" +
                           "\"appliedDate\":  \"2023-02-28T02:00:00\"," +
                           "\"approvalDate\":  \"2023-02-28T02:00:00\"" +
                           "}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("Failed to read HTTP message", map.get("message"));
                });
    }

    @Test
    @Override
    public void upsertByState_UnauthenticatedUser_Returns401() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_UserWithoutCSRFToken_Returns403() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();

        // Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_InternalServerError_Returns500() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.findByState(stateName)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_Exists_Returns200WithList() {
        // Given
        SalesTaxTracking secondSalesTaxTracking = salesTaxTracking
                .withState(new State("NY", "05", "New York"));
        SalesTaxTrackingDto salesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        SalesTaxTrackingDto secondSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(secondSalesTaxTracking);

        List<SalesTaxTracking> salesTaxTrackingList = new ArrayList<>() {{
            add(salesTaxTracking);
            add(secondSalesTaxTracking);
        }};

        List<SalesTaxTrackingDto> salesTaxTrackingDtoList = new ArrayList<>() {{
            add(salesTaxTrackingDto);
            add(secondSalesTaxTrackingDto);
        }};

        // When
        when(salesTaxTrackingFacade.findAll()).thenReturn(Flux.fromIterable(salesTaxTrackingList));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .isEqualTo(salesTaxTrackingDtoList);
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_EmptyCollection_Returns200WithEmptyList() {
        // Given
        List<SalesTaxTrackingDto> salesTaxTrackingDtoList = new ArrayList<>();

        // When
        when(salesTaxTrackingFacade.findAll()).thenReturn(Flux.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .isEqualTo(salesTaxTrackingDtoList);
    }

    @Test
    @Override
    public void getAll_UnauthenticatedUser_Returns401() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_InternalServerError_Returns500() {
        // When
        when(salesTaxTrackingFacade.findAll()).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }


    @Test
    @Override
    @WithMockUser
    public void upsertByStateName_Exists_Returns200() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null);
        String state = newSalesTaxTracking.getState().getName();
        SalesTaxTracking originalSalesTaxTracking = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDtoSent);
        SalesTaxTracking receivedSalesTaxTrackingWithId = receivedSalesTaxTracking
                .withId(UUID.randomUUID().toString());

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(receivedSalesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.just(originalSalesTaxTracking));
        when(salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking)).thenReturn(Mono.just(receivedSalesTaxTrackingWithId));
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state).build())
                .bodyValue(salesTaxTrackingDtoSent)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByStateAbbreviation_Exists_Returns200() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null);
        String state = newSalesTaxTracking.getState().getAbbreviation();
        SalesTaxTracking originalSalesTaxTracking = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDtoSent);
        SalesTaxTracking receivedSalesTaxTrackingWithId = receivedSalesTaxTracking
                .withId(UUID.randomUUID().toString());

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(receivedSalesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.just(originalSalesTaxTracking));
        when(salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking)).thenReturn(Mono.just(receivedSalesTaxTrackingWithId));
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/state/" + state).build())
                .bodyValue(salesTaxTrackingDtoSent)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_NullHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRouter.getSalesTaxTrackingByStateRouterFunction(nullSalesTaxTrackingHandler);
        });

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_NullHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRouter.getSalesTaxTrackingByComplytIdRouterFunction(nullSalesTaxTrackingHandler);
        });

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_NullHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRouter.getAllSalesTaxTrackingRouterFunction(nullSalesTaxTrackingHandler);
        });

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_NullHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRouter.upsertSalesTaxTrackingRouterFunction(nullSalesTaxTrackingHandler);
        });

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    @Override
    @WithMockUser
    public void getAny_InvalidUrl_Returns404() {
        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "wrong/url").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void putAny_InvalidUrl_Returns404() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "wrong/url").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullPhysicalNexusTrackerDto_Returns400ValidationError() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        Set<String> expectedErrors = Set.of(
                "physicalNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto.withPhysicalNexusTracker(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullEstablishedDatePhysicalNexusTrackerDto_Returns400ValidationError() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withPhysicalNexusTracker(new PhysicalNexusTrackerDto(false, null));
        Set<String> expectedErrors = Set.of(
                "PhysicalNexusTracker.establishedDate " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullEconomicNexusTrackerDto_Returns400ValidationError() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        Set<String> expectedErrors = Set.of(
                "economicNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingDto.withEconomicNexusTracker(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullEstablishedDateEconomicNexusTrackerDto_Returns400ValidationError() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withEconomicNexusTracker(new EconomicNexusTrackerDto(false, null));
        Set<String> expectedErrors = Set.of(
                "EconomicNexusTracker.establishedDate " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen200Comment_Returns400ValidationError() {
        // Given
        String commentOfLength201 = " This sentence is absolutely 50 characters long! | This sentence is absolutely 50 characters long! | This sentence is absolutely 50 characters long! | This sentence is absolutely 50 characters long! |$";
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto.withComment(commentOfLength201);
        Set<String> expectedErrors = Set.of(
                "comment " + StringErrorMessages.MAX_200_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NewWithBlankComment_Returns201() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto.withComment("");
        SalesTaxTracking mappedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(givenSalesTaxTrackingDto);

        // When
        when(salesTaxTrackingFacade.findByState(stateName)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(mappedSalesTaxTracking)).thenReturn(Mono.just(mappedSalesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated().expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(givenSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullState_Returns400ValidationError() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(null);
        Set<String> expectedErrors = Set.of(
                "state " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("", "code", "name"));
        Set<String> expectedErrors = Set.of(
                "State.abbreviation " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "", "name"));
        Set<String> expectedErrors = Set.of(
                "State.code " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", ""));
        Set<String> expectedErrors = Set.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto(testUtilities.stringWithLength(257), "code", "name"));
        Set<String> expectedErrors = Set.of(
                "State.abbreviation " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", testUtilities.stringWithLength(257), "name"));
        Set<String> expectedErrors = Set.of(
                "State.code " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", testUtilities.stringWithLength(257)));
        Set<String> expectedErrors = Set.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto(null, "code", "name"));
        Set<String> expectedErrors = Set.of(
                "State.abbreviation " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", null, "name"));
        Set<String> expectedErrors = Set.of(
                "State.code " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
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
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", null));
        Set<String> expectedErrors = Set.of(
                "State.name " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/state/" + stateName)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }
}
