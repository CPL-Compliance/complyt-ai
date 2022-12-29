package com.complyt.v1.controllers.router.handler;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.v1.controllers.router.SalesTaxTrackingRouter;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.model.SalesTaxTrackingDto;
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
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(SalesTaxTrackingHandler.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
@ContextConfiguration(classes = {SalesTaxTrackingRouter.class, SalesTaxTrackingHandler.class})
public class SalesTaxTrackingHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    SalesTaxTracking salesTaxTracking;

    @BeforeEach
    void setUp() {
        salesTaxTracking = createSalesTaxTracking();
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false, null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(true, LocalDateTime.now());
        return new SalesTaxTracking(null, state, null,
                true, physicalNexusTracker, economicNexusTracker, null, true, LocalDateTime.now());
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
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/" + state).build())
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
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/" + state).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @WithUserDetails
    void upsert_NewSalesTaxTracking_SalesTaxTrackingReturned() {
        // Given
        String state = salesTaxTracking.getState().getName();
        SalesTaxTracking salesTaxTrackingWithId = salesTaxTracking.withId(UUID.randomUUID().toString());
        SalesTaxTrackingDto salesTaxTrackingDtoSent =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTrackingWithId);

        // When
        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.empty());
        when(salesTaxTrackingFacade.save(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/" + state).build())
                .bodyValue(salesTaxTrackingDtoSent)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(SalesTaxTrackingDto.class)
                .isEqualTo(expectedSalesTaxTrackingDto);
    }

//    @Test
//    @WithUserDetails
//    void upsert_SalesTaxTrackingUpdated_SalesTaxTrackingReturned() {
//        // Given
//        String state = salesTaxTracking.getState().getName();
//        SalesTaxTracking originalSalesTaxTracking = salesTaxTracking.withId(UUID.randomUUID().toString());
//        SalesTaxTracking newSalesTaxTracking = originalSalesTaxTracking.withApprovalDate(LocalDateTime.now());
//        SalesTaxTrackingDto salesTaxTrackingDtoSent =
//                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);
//        SalesTaxTrackingDto expectedSalesTaxTrackingDto =
//                SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(newSalesTaxTracking);
//
//        // When
//        when(salesTaxTrackingFacade.findByState(state)).thenReturn(Mono.just(originalSalesTaxTracking));
//        when(salesTaxTrackingFacade.update(newSalesTaxTracking, state)).thenReturn(Mono.just(newSalesTaxTracking));
//        when(salesTaxTrackingFacade.save(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
//
//        // Then
//        webTestClient
//                .mutateWith(csrf())
//                .put()
//                .uri(uriBuilder -> uriBuilder.path(SalesTaxTrackingRouter.BASE_URL + "/" + state).build())
//                .bodyValue(salesTaxTrackingDtoSent)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody(SalesTaxTrackingDto.class)
//                .isEqualTo(expectedSalesTaxTrackingDto);
//    }

}
