package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handlers.SalesTaxTrackingHandler;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.validators.ValidatorConfig;
import org.bson.types.ObjectId;
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
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@WithMockUser(username = "mock", password = "mock")
@ContextConfiguration(classes = {SalesTaxTrackingRouter.class, SalesTaxTrackingHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class SalesTaxTrackingRouterTest {

    SalesTaxTrackingRouter salesTaxTrackingRouter;

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    SalesTaxTracking salesTaxTracking;

    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        salesTaxTrackingRouter = new SalesTaxTrackingRouter();
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxTracking = objectStub.createSalesTaxTracking(new ObjectId().toString());
    }

    @Test
    @WithUserDetails
    void getOne_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {

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
    @WithUserDetails
    void getByComplytId_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {

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
    @WithUserDetails
    void getOne_SalesTaxTrackingDoesNotExist_Throws404NotFound() {

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
    @WithUserDetails
    void getByComplytId_SalesTaxTrackingDoesNotExist_Throws404NotFound() {

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
    @WithUserDetails
    void upsert_NewSalesTaxTracking_SalesTaxTrackingReturned() {
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
    @WithUserDetails
    void getAll_GetsAllSalesTaxTracking_ReturnsAllSalesTaxTracking() {
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
    @WithUserDetails
    void upsert_SalesTaxTrackingUpdated_SalesTaxTrackingReturned() {
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
        when(salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking, state)).thenReturn(Mono.just(receivedSalesTaxTrackingWithId));
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
    void getSalesTaxTrackingByStateRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
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
    void getSalesTaxTrackingByComplytIdRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
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
    void getAllSalesTaxTrackingRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
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
    void upsertSalesTaxTrackingRouterFunction_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        SalesTaxTrackingHandler nullSalesTaxTrackingHandler = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingRouter.upsertSalesTaxTrackingRouterFunction(nullSalesTaxTrackingHandler);
        });

        // Then
        assertEquals("salesTaxTrackingHandler is marked non-null but is null", nullPointerException.getMessage());
    }

}
