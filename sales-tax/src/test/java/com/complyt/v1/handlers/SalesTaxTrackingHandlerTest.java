package com.complyt.v1.handlers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.config.JacksonConfig;
import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
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
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(SalesTaxTrackingHandler.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
@ContextConfiguration(classes = {SalesTaxTrackingRouter.class,
        SalesTaxTrackingHandler.class,
        ApiExceptionConfig.class,
        GlobalExceptionHandler.class,
        GlobalErrorAttributes.class})
public class SalesTaxTrackingHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    SalesTaxTracking salesTaxTracking;

    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxTracking = objectStub.createSalesTaxTracking(new ObjectId().toString());
    }

    @Test
    @WithUserDetails
    void getOne_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {

        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        String state = expectedSalesTaxTrackingDto.getState().getName();

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
        UUID complytId = expectedSalesTaxTrackingDto.getComplytId();

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
        UUID complytId = expectedSalesTaxTrackingDto.getComplytId();

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

}
