package com.complyt.v1.routers;

import com.complyt.domain.customer.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.handlers.CustomerHandler;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.transaction.OptionalAddressDto;
import com.complyt.v1.validators.Patcher;
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
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
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

    @MockBean
    Patcher<CustomerDto> customerPatcher;

    @Autowired
    private WebTestClient webTestClient;

    private UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        customerDto = testUtilities.createCustomerDto(UUID.randomUUID().toString());
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
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "name " + StringErrorMessages.MINMAX_256_ERROR,
                "source " + StringErrorMessages.SINGLE_DIGIT_ERROR,
                "customerType " + DtoErrorMessages.NOT_NULL_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map,
                        Set.of("source " + DtoErrorMessages.CONFLICTED_WITH_URL_ERROR)));
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
                .value(map -> testUtilities.checkErrorMessages(map,
                        Set.of("externalId " + DtoErrorMessages.CONFLICTED_WITH_URL_ERROR)));
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
                    assertEquals(GenericErrorMessages.DATA_CONFLICT_ERROR, map.get("message"));
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
                    assertEquals(GenericErrorMessages.DATA_CONFLICT_ERROR, map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String source = "1";
        String externalId = "0";

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_UnsupportedMediaType_Returns415() {
        // Given
        String source = "1";
        String externalId = "0";


        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Unsupported data")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_BlankSource_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidSource = "";
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "source " + StringErrorMessages.SINGLE_DIGIT_ERROR));

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
        Set<String> expectedErrors = new HashSet<>(List.of("source " + StringErrorMessages.SINGLE_DIGIT_ERROR));

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
    public void upsertByExternalIdAndSource_MoreThenOneDigitSource_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        String invalidSource = "10";
        Set<String> expectedErrors = new HashSet<>(List.of("source " + StringErrorMessages.SINGLE_DIGIT_ERROR));

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
    public void upsertByExternalIdAndSource_BlankExternalId_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String nullExternalId = "";
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "externalId " + StringErrorMessages.MINMAX_256_ERROR));

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
        String externalIdWithLengthOf257 = testUtilities.stringWithLength(257);
        String source = customerDto.source();
        Set<String> expectedErrors = new HashSet<>(List.of("externalId " + StringErrorMessages.MINMAX_256_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
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
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "name " + StringErrorMessages.MINMAX_256_ERROR));

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
        String nameWithLengthOf257 = testUtilities.stringWithLength(257);
        Set<String> expectedErrors = new HashSet<>(List.of("name " + StringErrorMessages.MINMAX_256_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_BlankEmail_Returns201Created() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        CustomerDto givenCustomer = customerDto.withEmail("");

        // When
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomer);
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(givenCustomer)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .equals(customerDto);
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NotInFormatEmail_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        String propertyName = "jakarta.validation.constraints.Email.message";
        Set<String> expectedErrors = Set.of(UnitTestUtilities.extractStringFromJakartaProperties(propertyName));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withEmail("dhggnfgdfrthj"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen100Email_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();

        String propertyName = "jakarta.validation.constraints.Email.message";
        Set<String> expectedErrors = Set.of(UnitTestUtilities.extractStringFromJakartaProperties(propertyName));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto.withEmail("somesomesomesomesomesomesomesomesomesomesomesomesomesomesomesomesomesome@some.com"))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen100CountyAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", testUtilities.stringWithLength(101), "state", "street", "zip", false);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.county " + StringErrorMessages.MAX_100_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen20ZipInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", "county", "state", "street", testUtilities.stringWithLength(21), false);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.zip " + StringErrorMessages.MAX_20_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen50CountryInAddress_Returns400ValidationError() {
        /// Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", testUtilities.stringWithLength(51), "county", "state", "street", "zip", false);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.country " + StringErrorMessages.MAX_50_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen100CityInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto(testUtilities.stringWithLength(101), "country", "county", "state", "street", "zip", false);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.city " + StringErrorMessages.MAX_100_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen100StateInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", "county", testUtilities.stringWithLength(101), "street", "zip", false);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.state " + StringErrorMessages.MAX_100_ERROR));
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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_LengthGreaterThen200StreetInAddress_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        OptionalAddressDto givenAddress = new OptionalAddressDto("city", "country", "county", "state", testUtilities.stringWithLength(201), "zip", false);
        Set<String> expectedErrors = new HashSet<>(List.of("Address.street" + StringErrorMessages.MAX_200_ERROR));

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
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
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
                    assertEquals("[customerType may not be null]", message);
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
    @WithUserDetails
    public void upsertByExternalIdAndSource_PathVariableInvalid_Returns400() {
        // Given
        String nullExternalId = "null";
        String source = customerDto.source();
        Customer newCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Customer originalCustomer = newCustomer.withName("originalCustomer");
        when(customerFacade.findByExternalIdAndSource(nullExternalId, source)).thenReturn(Mono.just(originalCustomer));
        when(customerFacade.saveCustomer(newCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(newCustomer, originalCustomer)).thenReturn(Mono.just(newCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + nullExternalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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
    @WithUserDetails
    public void getByExternalIdAndSource_PathVariableInvalid_Returns400() {
        // Given
        String externalId = UUID.randomUUID().toString();
        String source = "sourceError";
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Given
        String complytId = "uuidError";
        UUID complytIdUUID = customerDto.complytId();
        when(customerFacade.findByComplytId(complytIdUUID)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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
        // TODO
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
    public void getAll_Exists_Returns200WithList() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id);
        List<Customer> allCustomers = new ArrayList<>() {{
            add(customer);
            add(secondCustomer);
        }};

        // When
        when(customerFacade.getAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(allCustomers));

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

    @Test
    @Override
    @WithUserDetails
    public void getAll_QueryParamInvalid_Returns400() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id);
        List<Customer> allCustomers = new ArrayList<>() {{
            add(customer);
            add(secondCustomer);
        }};

        // When
        when(customerFacade.getAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(allCustomers));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_EmptyCollection_Returns200WithEmptyList() {
        /// Given
        List<Customer> emptyCustomerList = new ArrayList<>();

        // When
        when(customerFacade.getAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(emptyCustomerList));

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
        when(customerFacade.getAll(0, 0)).thenReturn(Flux.fromIterable(emptyCustomerList));

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
        when(customerFacade.getAll(0, 0)).thenReturn(Flux.error(new OperationFailedException()));

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
    public void getAllBySource_QueryParamInvalid_Returns400() {
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
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Override
    public void getAllBySource_PathVariableInvalid_Returns400() {
        /// Given
        String source = customer.getSource();
        List<Customer> emptyCustomerList = new ArrayList<>();
        String size = "sizeError";

        // When
        when(customerFacade.getAllBySource(source)).thenReturn(Flux.fromIterable(emptyCustomerList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "?size=" + size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
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
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "externalTimestamps " + DtoErrorMessages.NOT_NULL_ERROR));


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
                        "    \"customerType\": \"RETAIL\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
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
                    assertTrue(message.contains("Timestamps.createdDate " + DtoErrorMessages.NOT_NULL_ERROR));
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
                    assertTrue(message.contains("Timestamps.updatedDate " + DtoErrorMessages.NOT_NULL_ERROR));
                    assertTrue(message.contains("Timestamps.updatedDate " + DtoErrorMessages.NOT_NULL_ERROR));
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
                    assertEquals("[Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR + "]", message);
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
                    assertEquals("[Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR + "]", message);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInCreatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "  \"externalTimestamps\":  {" +
                        "       \"createdDate\":  \"2023-02-29\", \n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInUpdatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-02-29T08:00:00.000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInCreatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00.999999999",
                        customerDto.externalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInUpdatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        customerDto.externalTimestamps().createdDate(),
                        "2023-03-27T17:00:00.999999999"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInCreatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.0000000000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInUpdatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.0000000000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInCreatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00Z",
                        customerDto.externalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInUpdatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        customerDto.externalTimestamps().createdDate(),
                        "2023-03-27T17:00:00Z"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInCreatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00+17:59",
                        customerDto.externalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInUpdatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        customerDto.externalTimestamps().createdDate(),
                        "2023-03-27T17:00:00+17:59"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInCreatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00-18:00",
                        customerDto.externalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInUpdatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        customerDto.externalTimestamps().createdDate(),
                        "2023-03-27T17:00:00-18"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InCreatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000 + 18:01\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InUpdatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000 + 18:01\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_JustDateWithNoTimeOffsetInUpdatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        customerDto.externalTimestamps().createdDate(),
                        "2023-03-27"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.NOT_NULL_ERROR,
                "Timestamps.createdDate " + DtoErrorMessages.NOT_NULL_ERROR));

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
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   },\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.NOT_NULL_ERROR,
                "Timestamps.updatedDate " + DtoErrorMessages.NOT_NULL_ERROR));
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
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   },\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"createdDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
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
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   },\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"createdDate\":  \"2023-01-24T08:00:00.000Z\"," +
                        "\"updatedDate\":  \"" + invalidTimestamp + "\"" +
                        "}}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");

                    assertEquals("[Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR + "]", message);
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
        Set<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));

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
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   },\n" +
                        "    \"internalTimestamps\":  {" +
                        "\"createdDate\":  \"" + invalidTimestamp + "\", " +
                        "\"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInCreatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "  \"internalTimestamps\":  {" +
                        "       \"createdDate\":  \"2023-02-29\", \n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "},\n" +
                        "  \"externalTimestamps\":  {" +
                        "       \"createdDate\":  \"2023-02-28\", \n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_29OfFebruaryNotInLeapYearInUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "  \"internalTimestamps\":  {" +
                        "       \"createdDate\":  \"2023-02-28\", \n" +
                        "       \"updatedDate\":  \"2023-02-29T08:00:00.000Z\"" +
                        "},\n" +
                        "  \"externalTimestamps\":  {" +
                        "       \"createdDate\":  \"2023-02-28\", \n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"" +
                        "}\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00.999999999",
                        customerDto.internalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_9DigitsAfterTheDotInSecondsInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        customerDto.internalTimestamps().createdDate(),
                        "2023-03-27T17:00:00.999999999"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInCreatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"internalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.00000000000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000\" \n" +
                        "   },\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_10DigitsAfterTheDotInSecondsInUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"internalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.0000000000\" \n" +
                        "   },\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00Z",
                        customerDto.internalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfZInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        customerDto.internalTimestamps().createdDate(),
                        "2023-03-27T17:00:00Z"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00+17:59",
                        customerDto.internalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfPlusTimeInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        customerDto.internalTimestamps().createdDate(),
                        "2023-03-27T17:00:00+17:59"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInCreatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00-18:00",
                        customerDto.internalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMinusTimeInUpdatedDateInInternalTimestamp_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        customerDto.internalTimestamps().createdDate(),
                        "2023-03-27T17:00:00-18:00"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InCreatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.createdDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"internalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000+18:01\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000\" \n" +
                        "   },\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_ZoneSetWithOffsetOfMoreThan18InUpdatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        HashSet<String> expectedErrors = new HashSet<>(List.of(
                "Timestamps.updatedDate " + DtoErrorMessages.ISO8601_FORMAT_ERROR));


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
                        "    \"internalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000+18:01\" \n" +
                        "   },\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "           \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "           \"updatedDate\":  \"2023-01-24T08:00:00.000Z\" \n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> testUtilities.checkErrorMessages(map, expectedErrors));
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_JustDateWithNoTimeOffsetInUpdatedDateInInternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        customerDto.internalTimestamps().createdDate(),
                        "2023-03-27"
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_JustDateWithNoTimeOffsetInCreatedDateInInternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withInternalTimestamps(
                new TimestampsDto(
                        "2023-03-27T17:00:00.999999999",
                        customerDto.internalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }


    @Override
    @Test
    @WithMockUser
    public void upsert_JustDateWithNoTimeOffsetInCreatedDateInExternalTimestamps_Returns200Ok() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();

        CustomerDto givenCustomerDto = customerDto.withExternalTimestamps(
                new TimestampsDto(
                        "2023-03-27",
                        customerDto.externalTimestamps().updatedDate()
                ));
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);
        CustomerDto expectedCustomer = CustomerMapper.INSTANCE.customerToCustomerDto(receivedCustomer);

        // When + Then
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(customer));
        when(customerFacade.saveCustomer(receivedCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(receivedCustomer, customer)).thenReturn(Mono.just(receivedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenCustomerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomer));
    }

    // Patch

//    @Test
//    @WithMockUser
//    public void patch_PatchingByFewFields_Returns200() {
//        // Given
//        String now = LocalDateTime.now().toString();
//        CustomerTypeDto customerTypePatch = CustomerTypeDto.RETAIL_EXEMPT;
//        LinkedHashMap<String, Object> externalTimestampsToPatch = new LinkedHashMap<>() {{
//            put("createdDate", now);
//            put("updatedDate", now);
//        }};
//        TimestampsDto timestampsDto = new TimestampsDto(now, now);
//        Map<String, Object> map = new HashMap<>() {{
//            put("externalTimestamps", externalTimestampsToPatch);
//            put("customerType", customerTypePatch);
//        }};
//        CustomerDto expectedCustomerDto = customerDto
//                .withExternalTimestamps(timestampsDto)
//                .withCustomerType(customerTypePatch);
//
//        Customer expectedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(expectedCustomerDto);
//
//        // When + Then
//        when(customerFacade.findByComplytId(customerDto.complytId())).thenReturn(Mono.just(customer));
//        when(customerFacade.updateIfModified(expectedCustomer, customer)).thenReturn(Mono.just(expectedCustomer));
//        when(customerPatcher.patch(customerDto, map)).thenReturn(expectedCustomerDto);
//
//        webTestClient
//                .mutateWith(csrf())
//                .patch()
//                .uri(uriBuilder -> uriBuilder
//                        .path(CustomerRouter.BASE_URL + "/complytId/" + customerDto.complytId().toString())
//                        .build())
//                .bodyValue(map)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(CustomerDto.class)
//                .value(returnedCustomer -> returnedCustomer, equalTo(expectedCustomerDto));
//    }


    @Test
    public void patch_NullHandler_ThrowsNullPointerException() {
        // Given
        CustomerHandler nullCustomerHandler = null;
        CustomerRouter customerRouter = new CustomerRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            customerRouter.patchCustomerByComplytIdRouterFunction(nullCustomerHandler);
        });

        // Then
        assertEquals("customerHandler is marked non-null but is null", exception.getMessage());
    }

}