package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.handlers.CustomerHandler;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.models.OptionalAddressDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.validators.ValidatorConfig;
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
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


@WebFluxTest
@ContextConfiguration(classes = {CustomerRouter.class, CustomerHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
class CustomerRouterTest implements CustomerRouterTestTemplate {
    Customer customer;

    CustomerDto customerDto;

    @Autowired
    CustomerRouter customerRouter;

    @MockBean
    private CustomerFacade customerFacade;

    @Autowired
    private WebTestClient webTestClient;

    private TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customerDto = testUtilities.createCustomerDto(UUID.randomUUID().toString())
                .withExternalTimestamps(null)
                .withInternalTimestamps(null);
        customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .equals(customerDto);
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_CoupleValidationsFailure_Returns400WithErrorList() {
        // Given
        CustomerDto givenCustomerDto = customerDto
                .withName("")
                .withSource("d")
                .withCustomerType(null);
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Name may not be blank",
                "Name should be 1-256 characters maximum",
                "Source should be a single digit",
                "Customer type may not be null"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_DifferentSourceInBody_Returns400ConflictedData() {
        // Given
        String externalId = customerDto.externalId();
        String differentSource = "9";
        // When
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalIdAndSource(externalId, differentSource)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_DifferentExternalIdInBody_Returns400ConflictedData() {
        // Given
        String source = customerDto.source();
        String differentExternalId = UUID.randomUUID().toString();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + differentExternalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_ExistWithDifferentComplytId_Returns400ConflictedData() {
        // Given
        String source = customerDto.source();
        String externalId = customerDto.externalId();
        UUID differentComplytId = UUID.randomUUID();

        // When
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Customer differentCustomer = mappedCustomer.withComplytId(differentComplytId);
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(differentCustomer));
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(mappedCustomer, differentCustomer)).thenReturn(Mono.error(new ConflictedDataApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistAndHasComplytId_Returns400ConflictedData() {
        // Given
        String source = customerDto.source();
        String externalId = customerDto.externalId();

        // When
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.error(new ConflictedDataApiException()));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_BlankSource_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidSource = "";
        HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.addAll(List.of(
                "Source may not be blank",
                "Source should be a single digit"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withSource(invalidSource))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_nonDigitSource_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidSource = "y";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withSource(invalidSource))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Source should be a single digit]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_MoreThenOneDigitSource_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidSource = "10";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withSource(invalidSource))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Source should be a single digit]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_BlankExternalId_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String nullExternalId = "";
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.addAll(List.of(
                "External ID may not be blank",
                "External ID should be 1-256 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withExternalId(nullExternalId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_LengthGreaterThen256ExternalId_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String externalIdWithLengthOf257 = "baaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaab1";
        String source = customerDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withExternalId(externalIdWithLengthOf257))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[External ID should be 1-256 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_ComplytIdFailedToParse_Returns400() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidComplytId = "2d7dd100-3389-4b08-8b05-38dc88a114dx";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"complytId\": \"" + invalidComplytId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"externalTimestamps\":  {" +
                        "\"createdDate\":  \"2023-01-24T08:00:00.000Z\"," +
                        "\"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("Failed to read HTTP message", message);
                });
    }

    @Override
    @Test
    public void upsertByExternalIdAndSource_UnauthenticatedUser_Returns401() {
        // Given
        String source = customerDto.source();
        String externalId = customerDto.externalId();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_UserWithoutCSRFToken_Returns403() {
        /// Given
        String source = customerDto.source();
        String externalId = customerDto.externalId();

        // When + Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Override
    @Test
    @WithMockUser
    public void getAny_InvalidUrl_Returns404() {
        /// Given

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "invalid/url")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser()
    public void putAny_InvalidUrl_Returns404() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/non-existing-path/error")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_BlankName_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidName = "";
        HashSet<String> expectedErrors = new HashSet<String>();
        expectedErrors.addAll(List.of(
                "Name may not be blank",
                "Name should be 1-256 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withName(invalidName))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen256Name_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String nameWithLengthOf257 = "baaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaab1";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withName(nameWithLengthOf257))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Name should be 1-256 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen256CountyAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", "baaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaab1", "state", "street", "zip");
        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withAddress(givenAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[County should be 1-256 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen20ZipInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", "county", "state", "street", "bacabbacabbacabbacab$");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withAddress(givenAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[ZIP should be 1-20 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen256CountryInAddress_Returns400ValidationError() {
        /// Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "baaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaab1", "county", "state", "street", "zip");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withAddress(givenAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Country should be 1-256 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen256CityInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("baaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaab1", "country", "county", "state", "street", "zip");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withAddress(givenAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[City should be 1-256 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen256StateInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", "county", "baaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaab1", "street", "zip");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withAddress(givenAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[State should be 1-256 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen256StreetInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", "county", "state", "baaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaabbaaccaab1", "zip");

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withAddress(givenAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Street should be 1-256 characters maximum]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NullCustomerType_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withCustomerType(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Customer type may not be null]", message);
                });
    }

    @Test
    @Override
    @WithMockUser()
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();
        Customer newCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Customer originalCustomer = newCustomer.withName("originalCustomer");
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(originalCustomer));
        when(customerFacade.saveCustomer(newCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(newCustomer, originalCustomer)).thenReturn(Mono.just(newCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody().consumeWith(body -> System.out.println(body));
    }

    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = UUID.randomUUID().toString();
        String source = customerDto.source();
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(customerDto);
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_Exists_Returns200() {
        // Given
        UUID complytId = UUID.randomUUID();
        when(customerFacade.findByComplytId(complytId)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(customerDto);
    }

    @Test
    @Override
    @WithUserDetails
    public void upsertByExternalIdAndSource_InternalServerError_Returns500() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);

        // When
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenThrow(OperationFailedException.class);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithUserDetails
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        // Given
        String externalId = UUID.randomUUID().toString();
        String source = customerDto.source();
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Override
    @Test
    public void getByExternalIdAndSource_UnauthenticatedUser_Returns401() {
        // Given
        String externalId = customerDto.toString();
        String source = customerDto.source();

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void getByExternalIdAndSource_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Override
    @Test
    @WithMockUser
    public void getByExternalIdAndSource_InternalServerError_Returns500() {
        /// Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @WithUserDetails
    public void getByComplytId_DoesntExists_Returns404() {
        // Given
        UUID complytId = UUID.randomUUID();
        when(customerFacade.findByComplytId(complytId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CustomerRouter.BASE_URL + "/complytId/" + complytId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Override
    @Test
    public void getByComplytId_UnauthenticatedUser_Returns401() {
        // Given
        UUID complytId = UUID.randomUUID();
        when(customerFacade.findByComplytId(complytId)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CustomerRouter.BASE_URL + "/complytId/" + complytId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void getByComplytId_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Override
    @Test
    @WithMockUser
    public void getByComplytId_InternalServerError_Returns500() {
        /// Given
        UUID complytId = customerDto.complytId();

        // When
        when(customerFacade.findByComplytId(complytId)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithUserDetails
    public void getByName_Exists_Returns200WithList() {
        // Given
        String name = "name";
        List<Customer> customersFoundByName = new ArrayList<>() {{
            add(customer);
        }};

        // WHen
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(customersFoundByName));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(customersFoundByName);
    }

    @Override
    @Test
    @WithMockUser
    public void getByName_EmptyCollection_Returns200WithEmptyList() {
        // Given
        String name = "name";
        List<Customer> emptyCustomerList = new ArrayList<>();

        // When
        when(customerFacade.findByName(name)).thenReturn(Flux.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(emptyCustomerList);
    }

    @Override
    @Test
    public void getByName_UnauthenticatedUser_Returns401() {
        // Given
        String name = "name";
        List<Customer> customersFoundByName = new ArrayList<>() {{
            add(customer);
        }};

        // When
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(customersFoundByName));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void getByName_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Override
    @Test
    @WithMockUser
    public void getByName_InternalServerError_Returns500() {
        // Given
        String name = "name";

        // When
        when(customerFacade.findByName(name)).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithUserDetails
    public void getAll_Exists_Returns200WithList() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id);
        List<Customer> allCustomers = new ArrayList<>() {{
            add(customer);
            add(secondCustomer);
        }};

        // When
        when(customerFacade.getAll()).thenReturn(Flux.fromIterable(allCustomers));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(allCustomers);
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_EmptyCollection_Returns200WithEmptyList() {
        /// Given
        List<Customer> emptyCustomerList = new ArrayList<>();

        // When
        when(customerFacade.getAll()).thenReturn(Flux.fromIterable(emptyCustomerList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(emptyCustomerList);
    }

    @Override
    @Test
    public void getAll_UnauthenticatedUser_Returns401() {
        /// Given
        List<Customer> emptyCustomerList = new ArrayList<>();

        // When
        when(customerFacade.getAll()).thenReturn(Flux.fromIterable(emptyCustomerList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_InternalServerError_Returns500() {
        // Given + When
        when(customerFacade.getAll()).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @Override
    @WithUserDetails
    public void getAllBySource_Exists_Returns200WithList() {
        // Given
        String id = UUID.randomUUID().toString();
        String source = customer.getSource();
        Customer secondCustomer = customer.withId(id);

        List<Customer> allCustomers = new ArrayList<>() {{
            add(customer);
            add(secondCustomer);
        }};

        when(customerFacade.getAllBySource(source)).thenReturn(Flux.fromIterable(allCustomers));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(allCustomers);
    }

    @Override
    @Test
    @WithMockUser
    public void getAllBySource_EmptyCollection_Returns200WithEmptyList() {
        /// Given
        String source = customer.getSource();
        List<Customer> emptyCustomerList = new ArrayList<>();

        // When
        when(customerFacade.getAllBySource(source)).thenReturn(Flux.fromIterable(emptyCustomerList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(emptyCustomerList);
    }

    @Override
    @Test
    public void getAllBySource_UnauthenticatedUser_Returns401() {
        /// Given
        String source = customer.getSource();
        List<Customer> emptyCustomerList = new ArrayList<>();

        // When
        when(customerFacade.getAllBySource(source)).thenReturn(Flux.fromIterable(emptyCustomerList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void getAllBySource_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Override
    @Test
    @WithMockUser
    public void getAllBySource_InternalServerError_Returns500() {
        /// Given
        String source = customer.getSource();

        // When
        when(customerFacade.getAllBySource(source)).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    public void getAll_NullHandler_ThrowsNullPointerException() {
        // Given
        CustomerHandler nullCustomerHandler = null;
        CustomerRouter customerRouter = new CustomerRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            customerRouter.getAllCustomersRouterFunction(nullCustomerHandler);
        });

        // Then
        assertEquals("customerHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void getByExternalIdAndSource_NullHandler_ThrowsNullPointerException() {
        // Given
        CustomerHandler nullCustomerHandler = null;
        CustomerRouter customerRouter = new CustomerRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            customerRouter.getCustomerByExternalIdRouterFunction(nullCustomerHandler);
        });

        // Then
        assertEquals("customerHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void getAllBySource_NullHandler_ThrowsNullPointerException() {
        // Given
        CustomerHandler nullCustomerHandler = null;
        CustomerRouter customerRouter = new CustomerRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            customerRouter.getAllCustomersBySourceRouterFunction(nullCustomerHandler);
        });

        // Then
        assertEquals("customerHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void getByComplytId_NullHandler_ThrowsNullPointerException() {
        // Given
        CustomerHandler nullCustomerHandler = null;
        CustomerRouter customerRouter = new CustomerRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            customerRouter.getCustomerByComplytIdRouterFunction(nullCustomerHandler);
        });

        // Then
        assertEquals("customerHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void getByName_NullHandler_ThrowsNullPointerException() {
        // Given
        CustomerHandler nullCustomerHandler = null;
        CustomerRouter customerRouter = new CustomerRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            customerRouter.getCustomerByNameRouterFunction(nullCustomerHandler);
        });

        // Then
        assertEquals("customerHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void upsertByExternalIdAndSource_NullHandler_ThrowsNullPointerException() {
        // Given
        CustomerHandler nullCustomerHandler = null;
        CustomerRouter customerRouter = new CustomerRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            customerRouter.upsertCustomerByExternalIdRouterFunction(nullCustomerHandler);
        });

        // Then
        assertEquals("customerHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NullExternalTimestamps_Returns400ValidationError() {
        // Currently externalTimestamp can be null

    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NullCreatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"externalTimestamps\":  {" +
                        "\"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Created date may not be null]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NullUpdatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"externalTimestamps\":  {" +
                        "\"createdDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Updated date may not be null]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_BlankTimestampInUpdatedDateInExternalTimestamps_Returns400ValidationError() {
// Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String timestampWithLengthOf257 = "not a timestamp";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"externalTimestamps\":  {" +
                        "\"createdDate\":  \"2023-01-24T08:00:00.000Z\"," +
                        "\"updatedDate\":  \"" + timestampWithLengthOf257 + "\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Timestamp may not be blank]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_BlankTimestampInCreatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidTimestamp = "not a timestamp";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"externalTimestamps\":  {" +
                        "\"createdDate\":  \"" + invalidTimestamp + "\", " +
                        "\"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Timestamp may not be blank]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Created date may not be null]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"createdDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Updated date may not be null]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_BlankTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidTimestamp = "not a timestamp";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"createdDate\":  \"2023-01-24T08:00:00.000Z\"," +
                        "\"updatedDate\":  \"" + invalidTimestamp + "\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Timestamp may not be blank]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_BlankTimestampInCreatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidTimestamp = "not a timestamp";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n    \"externalId\": \"" + externalId + "\",\n" +
                        "    \"source\": \"" + source + "\",\n" +
                        "    \"name\": \"name\",\n" +
                        "    \"address\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"customerType\": \"RETAIL\",\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"createdDate\":  \"" + invalidTimestamp + "\", " +
                        "\"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Timestamp may not be blank]", message);
                });
    }
}