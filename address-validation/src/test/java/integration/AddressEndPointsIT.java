package integration;

import io.complyt.AddressValidationApplication;
import io.complyt.business.webclients.addressvalidations.HereStubAddressValidationWebClientWrapper;
import io.complyt.config.web_clients.WebClientWrapperProperties;
import io.complyt.domain.Address;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.here.HereAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.domain.here.HereFieldScore;
import io.complyt.security.TenantResolver;
import io.complyt.v1.mappers.AddressMapper;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.models.CachedAddressDataDto;
import io.complyt.v1.models.ValidatedAddressDto;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import test_utils.TestUtilities;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = AddressValidationApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles({"stubHere"})
public class AddressEndPointsIT extends TestContainersInitializerIT {

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @MockBean
    HereStubAddressValidationWebClientWrapper stubHereAddressValidationWebClientWrapper;

    @MockBean
    WebClientWrapperProperties hereWebClientWrapperProperties;

    @MockBean
    TenantResolver tenantResolver;

    @Autowired
    private WebTestClient webTestClient;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("address_validation"));
    }

    @BeforeEach
    void setup() {
        when(tenantResolver.resolve()).thenReturn(Mono.just("it_tenant"));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressFound_Returns200() {
        // Given
        AddressDto addressDto = new AddressDto(
                "New York", "United States", "New York", "New York",
                "164 Mulberry St", "10014", false);
        FieldsMatchScore fieldsMatchScore = new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT);
        CachedAddressDataDto expectedAddress = new CachedAddressDataDto(addressDto.withIsPartial(null), TestUtilities.getScoringDto().withFieldScore(fieldsMatchScore));

        // When + Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL + "/resolve")
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CachedAddressDataDto.class)
                .value(receivedAddressDto -> assertEquals(expectedAddress, receivedAddressDto));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressByStubClientWrapper_Returns200() {
        // Given
        AddressDto addressDto = new AddressDto(
                "Oxenfurt", "USA", "Gustfields", "NY",
                "Oxenfurt Academy", "11221", false);
        CachedAddressDataDto expectedAddress = new CachedAddressDataDto(addressDto.withIsPartial(null), TestUtilities.getScoringDto());

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
                        .path(AddressRouter.BASE_URL + "/resolve")
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CachedAddressDataDto.class)
                .value(receivedAddressDto -> assertEquals(expectedAddress, receivedAddressDto));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressByStubClientWrapper_DifferentState_Returns400() {
        // Given
        AddressDto addressDto = new AddressDto(
                "Oxenfurt", "USA", "Gustfields", "TX",
                "Oxenfurt Academy", "11221", false);
        CachedAddressDataDto expectedAddress = new CachedAddressDataDto(addressDto, TestUtilities.getScoringDto());

        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);

        HereAddressData hereAddressData = new HereAddressData(List.of(TestUtilities.getHereAddressItem()
                .withAddress(new HereAddress(null, null, address.country(), null, "Other State", address.county(), address.city(), address.street(), address.zip()))
                .withScoring(TestUtilities.getHereScoring().withQueryScore(1.0).withFieldScore(new HereFieldScore(1, 0, 0, null, 1)))));


        // When
        when(stubHereAddressValidationWebClientWrapper.validateAddress(any())).thenReturn(Mono.just(hereAddressData));

        // Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL + "/resolve")
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
                "Oxenfurt", "USA", "Gustfields", "NY",
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
                        .path(AddressRouter.BASE_URL + "/resolve")
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

    @Order(2)
    @Test
    @WithMockUser
    public void getAddress_AddressByOutsource_AddressNotFoundAndCachedAddressHasNoStreetWithBadScore_Returns200() {
        // Given
        AddressDto addressDto = new AddressDto(
                "Oxenfurt", "USA", "Gustfields", "NY",
                "Oxenfurt Academy", "11221", false);

        Address address = AddressMapper.INSTANCE.addressDtoToAddress(addressDto);
        double badScore = 0.1;
        HereAddressData hereAddressDataNoStreetBadScore = new HereAddressData(List.of(TestUtilities.getHereAddressItem()
                .withAddress(new HereAddress(null, null, address.country(), null, address.state(), address.county(), address.city(), null, address.zip()))
                .withScoring(TestUtilities.getHereScoring().withQueryScore(badScore))));

        HereAddressData hereAddressDataNoStreetGoodScore = new HereAddressData(List.of(TestUtilities.getHereAddressItem()
                .withAddress(new HereAddress(null, null, address.country(), null, address.state(), address.county(), address.city(), null, address.zip()))
                .withScoring(TestUtilities.getHereScoring().withQueryScore(1.0))));
        CachedAddressDataDto expectedAddress = new CachedAddressDataDto(addressDto.withIsPartial(null), TestUtilities.getScoringDto());

        // When
        when(stubHereAddressValidationWebClientWrapper.validateAddress(address)).thenReturn(Mono.just(hereAddressDataNoStreetBadScore));
        when(stubHereAddressValidationWebClientWrapper.validateAddress(address.withStreet(null))).thenReturn(Mono.just(hereAddressDataNoStreetGoodScore));

        // Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL + "/resolve")
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CachedAddressDataDto.class)
                .value(receivedAddressDto -> assertEquals(expectedAddress, receivedAddressDto));
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_AddressNotInUS_Returns400() {
        // Given
        AddressDto addressDto = new AddressDto(
                null, "Israel", null, null,
                null, "10014", true);
        FieldsMatchScore fieldsMatchScore = new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT);
        CachedAddressDataDto expectedAddress = new CachedAddressDataDto(addressDto, TestUtilities.getScoringDto().withFieldScore(fieldsMatchScore));

        // When + Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL + "/resolve")
                        .queryParam("country", addressDto.country())
                        .queryParam("isPartial", addressDto.isPartial())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(1)
    @Test
    @WithMockUser
    public void getAddress_ValidateAddress_Returns200() {
        // Given
        AddressDto addressDto = new AddressDto(
                "New York", "United States", null, "New York",
                "164 Mulberry St", "10014", false);
        FieldsMatchScore fieldsMatchScore = new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT);
        CachedAddressDataDto cachedAddressDataDto = new CachedAddressDataDto(addressDto.withCounty("New York").withIsPartial(null), TestUtilities.getScoringDto().withFieldScore(fieldsMatchScore));
        ValidatedAddressDto expectedAddress = new ValidatedAddressDto(List.of(cachedAddressDataDto), addressDto);

        // When + Then
        webTestClient
                .mutate().responseTimeout(Duration.ofSeconds(20)).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(AddressRouter.BASE_URL + "/validate")
                        .queryParam("state", addressDto.state())
                        .queryParam("zip", addressDto.zip())
                        .queryParam("city", addressDto.city())
                        .queryParam("street", addressDto.street())
                        .queryParam("country", addressDto.country())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ValidatedAddressDto.class)
                .value(receivedAddressDto -> assertEquals(expectedAddress, receivedAddressDto));
    }
}
