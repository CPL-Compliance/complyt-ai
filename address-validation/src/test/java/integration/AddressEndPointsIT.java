package integration;

import io.complyt.AddressValidationApplication;
import io.complyt.business.webclients.addressvalidations.AddressValidationWebClientWrapperBase;
import io.complyt.config.web_clients.WebClientWrapperProperties;
import io.complyt.domain.Address;
import io.complyt.domain.here.HereAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.v1.mappers.AddressMapper;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.routers.AddressRouter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test_utils.TestUtilities;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = AddressValidationApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddressEndPointsIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @MockBean
    AddressValidationWebClientWrapperBase stubHereAddressValidationWebClientWrapper;
    @MockBean
    WebClientWrapperProperties hereWebClientWrapperProperties;

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () ->  MONGO_CONTAINER.getReplicaSetUrl("address_validation"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressFound_Returns200() {
        // Given
        AddressDto addressDto = new AddressDto(
                "New York", "United States", "New York", "New York",
                "164 Mulberry St", "10014", false);

        // When + Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressDto.class)
                .value(receivedAddressDto -> assertEquals(addressDto, receivedAddressDto));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressByStubClientWrapper_Returns200() {
        // Given
        AddressDto addressDto = new AddressDto(
                "Oxenfurt", "The Continent", "Gustfields", "NY",
                "Oxenfurt Academy", "11221", false);

        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        HereAddressData hereAddressData = new HereAddressData(List.of(TestUtilities.getHereAddressItem()
                .withAddress(new HereAddress(null, null, address.country(), null, address.state(), address.county(), address.city(), address.street(), address.zip()))
                .withScoring(TestUtilities.getHereScoring().withQueryScore(1.0))));


        // When
        when(stubHereAddressValidationWebClientWrapper.validateAddress(any())).thenReturn(Mono.just(hereAddressData));

        // Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressDto.class)
                .value(receivedAddressDto -> assertEquals(addressDto, receivedAddressDto));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressByOutsource_ZipValidAndSubZip_Returns200() {
        // Given
        AddressDto addressDto = new AddressDto(
                "Oxenfurt", "The Continent", "Gustfields", "NY",
                "Oxenfurt Academy", "11222", false);

        String zipHere = "11222-1234";
        HereAddressData hereAddressData = new HereAddressData(List.of(TestUtilities.getHereAddressItem()
                .withAddress(new HereAddress(null, null, addressDto.country(), null, addressDto.state(), addressDto.county(), addressDto.city(), addressDto.street(), zipHere))
                .withScoring(TestUtilities.getHereScoring().withQueryScore(1.0))));

        AddressDto expectedAddressDto = addressDto.withZip(zipHere);

        // When
        when(stubHereAddressValidationWebClientWrapper.validateAddress(any())).thenReturn(Mono.just(hereAddressData));

        // Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AddressDto.class)
                .value(receivedAddressDto -> assertEquals(expectedAddressDto, receivedAddressDto));

    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressByOutsource_ZipHereNotMatches_Returns400() {
        // Given
        AddressDto addressDto = new AddressDto(
                "Oxenfurt", "The Continent", "Gustfields", "NY",
                "Oxenfurt Academy", "11222", false);

        String zipNotValid = "11111";

        HereAddressData hereAddressData = new HereAddressData(List.of(TestUtilities.getHereAddressItem()
                .withAddress(new HereAddress(null, null, addressDto.country(), null, addressDto.state(), addressDto.county(), addressDto.city(), addressDto.street(), zipNotValid))
                .withScoring(TestUtilities.getHereScoring().withQueryScore(1.0))));

        // When
        when(stubHereAddressValidationWebClientWrapper.validateAddress(any())).thenReturn(Mono.just(hereAddressData));

        // Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressByOutsource_ScoreNotValid_Returns400() {
        // Given
        AddressDto addressDto = new AddressDto(
                "Oxenfurt", "The Continent", "Gustfields", "NY",
                "Oxenfurt Academy", "11222", false);


        double score = 0.2f;

        HereAddressData hereAddressData = new HereAddressData(List.of(TestUtilities.getHereAddressItem()
                .withAddress(new HereAddress(null, null, addressDto.country(), null, addressDto.state(), addressDto.county(), addressDto.city(), addressDto.street(), addressDto.zip()))
                .withScoring(TestUtilities.getHereScoring().withQueryScore(score))));

        // When
        when(stubHereAddressValidationWebClientWrapper.validateAddress(any())).thenReturn(Mono.just(hereAddressData));

        // Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL)
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }
}
