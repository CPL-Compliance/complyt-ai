package com.example.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.config.QueryParamsExtractorConfig;
import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.facade.AddressWithSalesTaxRatesFacade;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.handler.AddressWithSalesTaxRatesHandler;
import com.complyt.v1.mappers.AddressMapper;
import com.complyt.v1.mappers.AddressWithSalesTaxRatesMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.AddressWithSalesTaxRatesDto;
import com.complyt.v1.router.AddressWithSalesTaxRatesRouter;
import com.example.complyt.config.SecurityConfig;
import com.testUtils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {AddressWithSalesTaxRatesRouter.class, AddressWithSalesTaxRatesHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        QueryParamsExtractorConfig.class,
        SecurityConfig.class})
public class AddressWithSalesTaxRatesRouterTest {

    @Autowired
    AddressWithSalesTaxRatesRouter addressWithSalesTaxRatesRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AddressWithSalesTaxRatesFacade addressWithSalesTaxRatesFacade;

    @Test
    @WithMockUser
    public void findByAddress_Exists_Returns200() {
        // Given
        AddressDto addressDto = TestUtilities.createAddressDtoInCalifornia();
        AddressWithSalesTaxRates addressWithSalesTaxRates = TestUtilities.createCaliforniaAddressWithSalesTaxRates();
        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        // When
        when(addressWithSalesTaxRatesFacade.findByAddress(address)).thenReturn(Mono.just(addressWithSalesTaxRates));
        AddressWithSalesTaxRatesDto addressWithSalesTaxRatesDto = AddressWithSalesTaxRatesMapper.INSTANCE
                .addressWithSalesTaxRatesToAddressWithSalesTaxRatesDto(addressWithSalesTaxRates);

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressWithSalesTaxRatesRouter.BASE_URL)
                        .queryParam("state", addressDto.state())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("zip", addressDto.zip())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressWithSalesTaxRatesDto.class)
                .value(addressWithSalesTaxRatesItem -> addressWithSalesTaxRatesItem, equalTo(addressWithSalesTaxRatesDto));
    }
}
