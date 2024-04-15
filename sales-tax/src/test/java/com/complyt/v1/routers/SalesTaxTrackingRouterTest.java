package com.complyt.v1.routers;

import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.PatcherConfig;
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
import org.junit.jupiter.api.Assertions;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {SalesTaxTrackingRouter.class, SalesTaxTrackingHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class,
        PatcherConfig.class})
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
        String country = expectedSalesTaxTrackingDto.country();
        String state = expectedSalesTaxTrackingDto.state().name();

        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.just(salesTaxTracking));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", country)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_ExistsOutsideOfUSA_Returns200WithList() {
        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking)
                        .withCountry("Canada");
        String country = expectedSalesTaxTrackingDto.country();
        String state = "null";

        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.just(salesTaxTracking.withCountry("Canada")));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", country)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_QueryParamError_Returns400() {
        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        String state = "NoneState";
        String country = expectedSalesTaxTrackingDto.country();

        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.just(salesTaxTracking));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", country)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_UsaCountry_Returns201() {
        // Given
        SalesTaxTrackingDto newSalesTaxTrackingDto = salesTaxTrackingDto
                .withComplytId(null);
        SalesTaxTracking newSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(newSalesTaxTrackingDto);

        String country = newSalesTaxTrackingDto.country();
        String state = newSalesTaxTracking.getState().getName();

        SalesTaxTracking receivedSalesTaxTrackingWithId = newSalesTaxTracking
                .withId(UUID.randomUUID().toString());

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(receivedSalesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, newSalesTaxTracking.getState().getName())).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.just(receivedSalesTaxTrackingWithId));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder ->
                        uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                                .queryParam("country", country)
                                .queryParam("state", state)
                                .build())
                .bodyValue(newSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_NonUsaCountry_Returns201() {
        // Given
        SalesTaxTrackingDto newSalesTaxTrackingDto = salesTaxTrackingDto
                .withComplytId(null)
                .withState(null)
                .withCountry("Canada");
        SalesTaxTracking newSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(newSalesTaxTrackingDto);

        String country = newSalesTaxTrackingDto.country();

        SalesTaxTracking receivedSalesTaxTrackingWithId = newSalesTaxTracking
                .withId(UUID.randomUUID().toString());

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(receivedSalesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, null)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.just(receivedSalesTaxTrackingWithId));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder ->
                        uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                                .queryParam("country", country)
                                .build())
                .bodyValue(newSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
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
    public void getByComplytId_QueryParamInvalid_Returns400() {
        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        UUID complytId = expectedSalesTaxTrackingDto.complytId();
        String nullComplytId = "null";

        when(salesTaxTrackingFacade.findByComplytId(complytId)).thenReturn(Mono.just(salesTaxTracking));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/complytId/" + nullComplytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void getByState_DoesntExists_Returns404() {

        String country = salesTaxTrackingDto.country();
        String state = salesTaxTracking.getState().getName();

        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.empty());

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
        String country = "USA";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", country)
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

        String country = salesTaxTrackingDto.country();
        String state = salesTaxTrackingDto.state().name();

        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.error(new OperationFailedException()));

        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", country)
                        .build())
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
    public void
    upsertByState_OutsideOfUsaAndDoesntExists_Returns201() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null).withCountry("Canada").withState(null);
        String country = newSalesTaxTracking.getCountry();
        String state = null;
        SalesTaxTracking salesTaxTrackingWithId = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder ->
                        uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                                .queryParam("country", country)
                                .build())
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
    public void upsertByState_DoesntExists_Returns201() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null);
        String country = newSalesTaxTracking.getCountry();
        String state = newSalesTaxTracking.getState().getName();
        SalesTaxTracking salesTaxTrackingWithId = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder ->
                        uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                                .queryParam("state", state)
                                .queryParam("country", country)
                                .build())
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
    public void upsert_UnSupportedNonUsaCountrySentInBodyAndParam_Returns400() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDtoToSend = salesTaxTrackingDto.withCountry("Canadaa");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", salesTaxTrackingDtoToSend.state().name())
                        .queryParam("country", "Canadaa")
                        .build())
                .bodyValue(salesTaxTrackingDtoToSend)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_UnSupportedNonUsaCountrySentOnlyInBody_Returns400() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDtoToSend = salesTaxTrackingDto.withCountry("Canadaa");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", salesTaxTrackingDtoToSend.state().name())
                        .queryParam("country", "Canada")
                        .build())
                .bodyValue(salesTaxTrackingDtoToSend)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_UnSupportedNonUsaCountrySentOnlyInQueryParam_Returns400() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDtoToSend = salesTaxTrackingDto.withCountry("Canada");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", salesTaxTrackingDtoToSend.state().name())
                        .queryParam("country", "Canadaa")
                        .build())
                .bodyValue(salesTaxTrackingDtoToSend)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
//    @Override
    @WithMockUser
    public void upsert_BlankStateInQueryParam_Returns400() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingDtoToSend = salesTaxTrackingDto.withCountry("USA");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("statee", "")
                        .queryParam("country", salesTaxTrackingDtoToSend.state().name())
                        .build())
                .bodyValue(salesTaxTrackingDtoToSend)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_UsaCountryConflictCheck_Returns400() {
        // Given
        String country = "nonUsaCountry";
        Set<String> expectedErrors = Set.of(DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", "CA")
                        .queryParam("country", country)
                        .build())
                .bodyValue(salesTaxTrackingDto.withCountry("USA"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_NonUsaCountryConflictCheck_Returns400() {
        // Given
        String country = "nonUsCountry";
        Set<String> expectedErrors = Set.of(
                DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", country)
                        .build())
                .bodyValue(salesTaxTrackingDto.withCountry("Canada"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Test
    @Override
    @WithMockUser
    public void upsertByState_QueryParamError_Returns400() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null);
        String country = newSalesTaxTracking.getCountry();
        String state = newSalesTaxTracking.getState().getName();
        SalesTaxTracking salesTaxTrackingWithId = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        String stateError = "null";

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateError)
                        .queryParam("country", country)
                        .build())
                .bodyValue(salesTaxTrackingDtoSent)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_CoupleValidationsFailure_Returns400WithErrorList() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        String country = salesTaxTrackingDto.country();
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
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
    public void upsertByState_QueryParamStateIsBlank_Returns200Ok() {
        // Given
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", salesTaxTrackingDto.state().name()))
                .withCountry("Brazil");
        SalesTaxTracking mappedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(givenSalesTaxTrackingDto);
        UUID complytId = UUID.randomUUID();
        SalesTaxTracking originalSalesTaxTracking = mappedSalesTaxTracking.withComplytId(complytId);

        String country = givenSalesTaxTrackingDto.country();
        when(salesTaxTrackingFacade.findByCountryAndState(country, givenSalesTaxTrackingDto.state().name())).thenReturn(Mono.just(originalSalesTaxTracking));
        when(salesTaxTrackingFacade.save(mappedSalesTaxTracking)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.update(mappedSalesTaxTracking, originalSalesTaxTracking)).thenReturn(Mono.just(mappedSalesTaxTracking.withComplytId(complytId)));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("country", country)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class);
    }


    @Test
    @Override
    @WithMockUser
    public void upsertByState_DifferentStateInBody_Returns400ConflictedData() {
        // Given
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", salesTaxTrackingDto.state().name() + "boo"));
        String stateName = salesTaxTrackingDto.state().name();
        String country = salesTaxTrackingDto.country();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map,
                        Set.of("state " + DtoErrorMessages.CONFLICTED_WITH_QUERY_PARAM_IN_URL_ERROR)));
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_ExistWithDifferentComplytId_Returns400ConflictedData() {
        // Given
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();
        UUID differentComplytId = UUID.randomUUID();
        SalesTaxTracking differentSalesTaxTracking = salesTaxTracking.withComplytId(differentComplytId);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, stateName)).thenReturn(Mono.just(differentSalesTaxTracking));
        when(salesTaxTrackingFacade.update(salesTaxTracking, differentSalesTaxTracking)).thenReturn(Mono.error(new ConflictedDataApiException()));
        when(salesTaxTrackingFacade.save(salesTaxTracking)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, stateName)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(salesTaxTracking)).thenReturn(Mono.error(new ConflictedDataApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
    public void upsertByState_NotRegisteredButDateGiven_Returns400ConflictedData() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto salesTaxTrackingConflictedDto = salesTaxTrackingDto.withRegistered(null)
                .withRegistrationDate(LocalDateTime.now());

        // When
        when(salesTaxTrackingFacade.findByCountryAndState("USA", stateName)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingFacade.save(salesTaxTracking)).thenReturn(Mono.error(new ConflictedDataApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(salesTaxTrackingConflictedDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertTrue(map.get("message").toString().contains(GenericErrorMessages.CONFLICTED_REGISTERED_ERROR));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByState_ComplytIdFailedToParse_Returns400() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        String country = salesTaxTrackingDto.country();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();

        // Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, stateName)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        when(salesTaxTrackingFacade.findAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(salesTaxTrackingList));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/all").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(SalesTaxTrackingDto.class)
                .isEqualTo(salesTaxTrackingDtoList);
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_QueryParamInvalid_Returns400() {
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

        // When
        when(salesTaxTrackingFacade.findAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(salesTaxTrackingList));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/all")
                                .queryParam("page", "null")
                                .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_EmptyCollection_Returns200WithEmptyList() {
        // Given
        List<SalesTaxTrackingDto> salesTaxTrackingDtoList = new ArrayList<>();

        // When
        when(salesTaxTrackingFacade.findAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/all").build())
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
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/all").build())
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
        when(salesTaxTrackingFacade.findAll(0, 0)).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/all").build())
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
        String country = newSalesTaxTracking.getCountry();
        String state = newSalesTaxTracking.getState().getName();
        SalesTaxTracking originalSalesTaxTracking = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDtoSent);
        SalesTaxTracking receivedSalesTaxTrackingWithId = receivedSalesTaxTracking
                .withId(UUID.randomUUID().toString());

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(receivedSalesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.just(originalSalesTaxTracking));
        when(salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking)).thenReturn(Mono.just(receivedSalesTaxTrackingWithId));
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder ->
                        uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                                .queryParam("state", state)
                                .queryParam("country", country)
                                .build())
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
        String country = newSalesTaxTracking.getCountry();
        String state = newSalesTaxTracking.getState().getName();
        SalesTaxTracking originalSalesTaxTracking = newSalesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
        SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDtoSent);
        SalesTaxTracking receivedSalesTaxTrackingWithId = receivedSalesTaxTracking
                .withId(UUID.randomUUID().toString());

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(receivedSalesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.just(originalSalesTaxTracking));
        when(salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking)).thenReturn(Mono.just(receivedSalesTaxTrackingWithId));
        when(salesTaxTrackingFacade.save(newSalesTaxTracking)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder ->
                        uriBuilder.path(SalesTaxTrackingRouter.BASE_URL)
                                .queryParam("state", state)
                                .queryParam("country", country)
                                .build())
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
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                salesTaxTrackingRouter.getSalesTaxTrackingByStateRouterFunction(nullSalesTaxTrackingHandler));

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
    public void postAny_InvalidUrl_Returns404() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
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
        String country = salesTaxTrackingDto.country();
        Set<String> expectedErrors = Set.of(
                "physicalNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("/country/", country)
                        .queryParam("/state/", stateName)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withPhysicalNexusTracker(new PhysicalNexusTrackerDto(false, null));
        Set<String> expectedErrors = Set.of(
                "PhysicalNexusTracker.establishedDate " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        Set<String> expectedErrors = Set.of(
                "economicNexusTracker " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withEconomicNexusTracker(new EconomicNexusTrackerDto(false, null));
        Set<String> expectedErrors = Set.of(
                "EconomicNexusTracker.establishedDate " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto.withComment(commentOfLength201);
        Set<String> expectedErrors = Set.of(
                "comment " + StringErrorMessages.MAX_200_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto.withComment("");
        SalesTaxTracking mappedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(givenSalesTaxTrackingDto);

        // When
        when(salesTaxTrackingFacade.findByCountryAndState(country, stateName)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(mappedSalesTaxTracking)).thenReturn(Mono.just(mappedSalesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(givenSalesTaxTrackingDto);
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_ReturnsSalesTaxTracking_Returns200() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto resultedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> Assertions.assertEquals(resultedSalesTaxTracking, returnedSalesTaxTrackingDto));
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_NonUsaCountryReturnsSalesTaxTracking_Returns200() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = "Canada";
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto resultedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> Assertions.assertEquals(resultedSalesTaxTracking, returnedSalesTaxTrackingDto));
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByDate_ReturnsSalesTaxTracking_Returns200() {
        // Given
        LocalDate localDate = LocalDate.now();
        SalesTaxTracking salesTaxTrackingToSend = salesTaxTracking.withCountry("Canada").withState(null);
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();
        SalesTaxTrackingDto resultedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTrackingToSend);

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTrackingToSend));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTrackingDto -> Assertions.assertEquals(resultedSalesTaxTracking, returnedSalesTaxTrackingDto));
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_FacadeReturnsEmpty_Returns404NotFound() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.empty());

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + stateName)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_DateNotInFormat_Returns400() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .queryParam("date", localDate + "$")
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals("[date must be in the format yyyy-mm-dd]", map.get("message")));
        ;
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_NoDateInAsQueryParam_Returns400() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals("[date must be in the format yyyy-mm-dd]", map.get("message")));
    }

    @Test
    @Override
    public void refreshByStateAndDate_UnauthenticatedUser_Returns401() {
        // Given
        LocalDate localDate = LocalDate.now();
        String stateName = salesTaxTrackingDto.state().name();
        String country = salesTaxTrackingDto.country();

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + stateName)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_UserWithoutCSRFToken_Returns403() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        webTestClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh/state/" + stateName)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_QueryParamError_Returns400() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();
        String state = "null";

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("state", state)
                        .queryParam("country", country)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_InternalServerError_Returns500() {
        // Given
        LocalDate localDate = LocalDate.now();
        String country = salesTaxTrackingDto.country();
        String stateName = salesTaxTrackingDto.state().name();

        // When
        when(salesTaxTrackingFacade.refreshNexusSummary(country, stateName, localDate)).thenReturn(Mono.error(RuntimeException::new));

        // Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL + "/refresh")
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .queryParam("date", localDate)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithMockUser
    public void refreshByStateAndDate_NullHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                salesTaxTrackingRouter.refreshNexusSummaryByDateRouterFunction(nullSalesTaxTrackingHandler));

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", nullPointerException.getMessage());

    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullState_Returns400ValidationError() {
        // Given
        String stateName = salesTaxTrackingDto.state().name();
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(null);
        Set<String> expectedErrors = Set.of(DtoErrorMessages.STATE_MUST_NOT_BE_NULL_USA);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("/country/", country)
                        .queryParam("/state/", stateName)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("", "code", "name"));
        Set<String> expectedErrors = Set.of(
                "State.abbreviation " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "", "name"));
        Set<String> expectedErrors = Set.of(
                "State.code " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", ""));
        Set<String> expectedErrors = Set.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto(testUtilities.stringWithLength(257), "code", "name"));
        Set<String> expectedErrors = Set.of(
                "State.abbreviation " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", testUtilities.stringWithLength(257), "name"));
        Set<String> expectedErrors = Set.of(
                "State.code " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", testUtilities.stringWithLength(257)));
        Set<String> expectedErrors = Set.of(
                "State.name " + StringErrorMessages.MINMAX_256_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto(null, "code", "name"));
        Set<String> expectedErrors = Set.of(
                "State.abbreviation " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", null, "name"));
        Set<String> expectedErrors = Set.of(
                "State.code " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
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
        String country = salesTaxTrackingDto.country();
        SalesTaxTrackingDto givenSalesTaxTrackingDto = salesTaxTrackingDto
                .withState(new StateDto("CA", "code", null));
        Set<String> expectedErrors = Set.of(
                "State.name " + DtoErrorMessages.NOT_NULL_ERROR);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", stateName)
                        .queryParam("country", country)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenSalesTaxTrackingDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    // Patch
    @Test
    @WithMockUser
    public void patch_PatchingByFewFields_Returns200() {
        // Given
        LocalDateTime establishedDateToPatch = salesTaxTrackingDto.physicalNexusTracker().establishedDate().plusMonths(1);
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = salesTaxTrackingDto.physicalNexusTracker()
                .withEstablishedDate(establishedDateToPatch);
        LocalDateTime appliedDateToPatch = salesTaxTrackingDto.appliedDate().plusMonths(1);
        SalesTaxTrackingDto expectedSalesTaxTrackingDto = salesTaxTrackingDto
                .withPhysicalNexusTracker(physicalNexusTrackerToPatch)
                .withAppliedDate(appliedDateToPatch);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("physicalNexusTracker", physicalNexusTrackerToPatch);
            put("appliedDate", appliedDateToPatch);
        }};

        String country = salesTaxTrackingDto.country();
        String state = salesTaxTrackingDto.state().name();

        SalesTaxTracking expectedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(expectedSalesTaxTrackingDto);
        SalesTaxTracking originalSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto);

        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.just(originalSalesTaxTracking));
        when(salesTaxTrackingFacade.update(expectedSalesTaxTracking, originalSalesTaxTracking)).thenReturn(Mono.just(expectedSalesTaxTracking));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", country)
                        .build())
                .bodyValue(map)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTracking -> returnedSalesTaxTracking, equalTo(expectedSalesTaxTrackingDto));
    }

    @Test
    @WithMockUser
    public void patch_PatchingCountryDifferentThanUsa_Returns200() {
        // Given
        SalesTaxTrackingDto salesTaxTrackingOutsideOfUSA = salesTaxTrackingDto
                .withCountry("Canada");
        LocalDateTime establishedDateToPatch = salesTaxTrackingOutsideOfUSA.physicalNexusTracker().establishedDate().plusMonths(1);
        PhysicalNexusTrackerDto physicalNexusTrackerToPatch = salesTaxTrackingDto.physicalNexusTracker()
                .withEstablishedDate(establishedDateToPatch);

        SalesTaxTrackingDto expectedSalesTaxTrackingDto = salesTaxTrackingOutsideOfUSA
                .withPhysicalNexusTracker(physicalNexusTrackerToPatch);
        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("physicalNexusTracker", physicalNexusTrackerToPatch);
        }};

        String country = salesTaxTrackingOutsideOfUSA.country();
        String state = salesTaxTrackingOutsideOfUSA.state().name();

        SalesTaxTracking expectedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(expectedSalesTaxTrackingDto);
        SalesTaxTracking originalSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingOutsideOfUSA);

        when(salesTaxTrackingFacade.findByCountryAndState(country, state)).thenReturn(Mono.just(originalSalesTaxTracking));
        when(salesTaxTrackingFacade.update(expectedSalesTaxTracking, originalSalesTaxTracking)).thenReturn(Mono.just(expectedSalesTaxTracking));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path(SalesTaxTrackingRouter.BASE_URL)
                        .queryParam("state", state)
                        .queryParam("country", country)
                        .build())
                .bodyValue(map)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(SalesTaxTrackingDto.class)
                .value(returnedSalesTaxTracking -> returnedSalesTaxTracking, equalTo(expectedSalesTaxTrackingDto));
    }

    @Test
    public void patch_NullHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;
        SalesTaxTrackingRouter salesTaxTrackingRouter = new SalesTaxTrackingRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRouter.patchSalesTaxTrackingRouterFunction(nullSalesTaxTrackingHandler);
        });

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", exception.getMessage());
    }

}
