package com.example.complyt.v1.routers;

import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.facade.ComplytGtRatesFacade;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handler.ComplytGtRatesHandler;
import com.complyt.v1.mappers.ComplytGtRatesMapper;
import com.complyt.v1.model.gt.ComplytGtRatesDto;
import com.complyt.v1.router.GtRatesRouter;
import com.complyt.v1.validators.query_params.GtAddressQueryParamsExtractor;
import com.example.complyt.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import testUtils.TestUtilities;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {GtRatesRouter.class, ComplytGtRatesHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalExceptionHandler.class,
        GlobalErrorAttributes.class,
        SecurityConfig.class,
        GtAddressQueryParamsExtractor.class
})
public class GtRatesRouterTest {

    @Autowired
    GtRatesRouter gtRatesRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ComplytGtRatesFacade complytGtRatesFacade;

    @Test
    @WithMockUser
    public void findByAddress_ComplytGtRatesFound_Returns200() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress();
        ComplytGtRates complytGtRates = TestUtilities.createCanadaComplytGtRates();

        // When
        when(complytGtRatesFacade.findByAddress(gtAddress)).thenReturn(Mono.just(complytGtRates));
        ComplytGtRatesDto complytGtRatesDto = ComplytGtRatesMapper.INSTANCE
                .complytGstRatesToComplytGstRatesDto(complytGtRates);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GtRatesRouter.BASE_URL)
                        .queryParam("country", gtAddress.country())
                        .queryParam("region", gtAddress.region())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ComplytGtRatesDto.class)
                .value(returnedComplytGtRatesDto -> returnedComplytGtRatesDto, equalTo(complytGtRatesDto));
    }

    @Test
    @WithMockUser
    public void findByAddress_BlankCountryPassed_Returns400() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress().withCountry("");
        ComplytGtRates complytGtRates = TestUtilities.createCanadaComplytGtRates();

        // When
        when(complytGtRatesFacade.findByAddress(gtAddress)).thenReturn(Mono.just(complytGtRates));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GtRatesRouter.BASE_URL)
                        .queryParam("country", gtAddress.country())
                        .queryParam("region", gtAddress.region())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser
    public void findByAddress_NullCountryPassed_Returns400() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress();
        ComplytGtRates complytGtRates = TestUtilities.createCanadaComplytGtRates();

        // When
        when(complytGtRatesFacade.findByAddress(gtAddress)).thenReturn(Mono.just(complytGtRates));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GtRatesRouter.BASE_URL)
                        .queryParam("region", gtAddress.region())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser
    public void findByAddress_OverSizeCountryPassed_Returns400() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress()
                .withCountry("123456789012345678901234567890123456789012345678901");
        ComplytGtRates complytGtRates = TestUtilities.createCanadaComplytGtRates();

        // When
        when(complytGtRatesFacade.findByAddress(gtAddress)).thenReturn(Mono.just(complytGtRates));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GtRatesRouter.BASE_URL)
                        .queryParam("region", gtAddress.region())
                        .queryParam("country", gtAddress.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @WithMockUser
    public void findByAddress_OverSizeRegionPassed_Returns400() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress()
                .withRegion("123456789012345678901234567890123456789012345678901");
        ComplytGtRates complytGtRates = TestUtilities.createCanadaComplytGtRates();

        // When
        when(complytGtRatesFacade.findByAddress(gtAddress)).thenReturn(Mono.just(complytGtRates));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(GtRatesRouter.BASE_URL)
                        .queryParam("region", gtAddress.region())
                        .queryParam("country", gtAddress.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    public void findByAddress_NullHandler_ThrowsNullPointerException() {
        // Given
        ComplytGtRatesHandler nullComplytGtRatesHandler = null;
        GtRatesRouter complytGtRatesRouter = new GtRatesRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            complytGtRatesRouter.getGstRatesByAddress(nullComplytGtRatesHandler);
        });

        // Then
        assertEquals("complytGtRatesHandler " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE, exception.getMessage());
    }

}