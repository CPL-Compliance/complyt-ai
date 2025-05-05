package integration.endpoints;

import com.complyt.SalesTaxApplication;
import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.transaction.OptionalAddressDto;
import com.complyt.v1.routers.CustomerRouter;
import integration.TestContainersInitializerIT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import testUtils.integration_test.ITUtilities;
import testUtils.annotations.WithMockJwt;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = SalesTaxApplication.class)
@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CustomerEndpointsIT extends TestContainersInitializerIT implements CustomerEndpointsITITTemplate {

    @MockBean
    TenantResolver tenantResolver;
    @Autowired
    private WebTestClient webTestClient;

    // Given
    private final String source = "1";

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> MONGO_CONTAINER.getReplicaSetUrl("sales_tax"));
    }

    @Override
    @Test
    @Order(0)
    @WithMockJwt
    public void upsertByExternalIdAndSource_NoBody_Returns400() {
        // Given
        String externalId = "0";

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.MISSING_BODY_ERROR, map.get("message")));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_Exists_Returns200() {
        // Given
        String differentSource = "2";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertEquals(1, list.size()));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_QueryParamInvalid_Returns400() {
        // Given
        String differentSource = "2";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource)
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_PathVariableInvalid_Returns400() {
        // Given
        String sourceError = "null";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + sourceError)
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAllBySource_DoesntExists_Returns200EmptyList() {
        // Given
        String differentSource = "9";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertEquals(list.size(), 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAll_Exists_Returns200() {
        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertTrue(list.size() > 5));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getAll_QueryParamInvalid_Returns400() {
        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("size", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt(tenantId = "different_tenant")
    public void getByAll_DoesntExists_Returns200EmptyList() {

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list ->
                        assertEquals(list.size(), 0));
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt(tenantId = "different_tenant")
    public void getByAll_QueryParamInvalid_Returns400() {

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

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByComplytId_Exists_Returns200() {
        // Given
        String complytId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5"; //complytId of existing customer

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerDto ->
                        assertEquals(customerDto.complytId(), UUID.fromString(complytId)));
    }

    @Override
    @WithMockJwt
    public void getByComplytId_PathVariableInvalid_Returns400() {
        // Given
        String complytId = "null";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByComplytId_DoesntExists_Returns404() {
        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + ITUtilities.NON_EXISTING_COMPLYT_ID)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_UnsupportedMediaType_Returns415() {
        // Given
        String externalId = "1001";

        // Then
        webTestClient
                .mutateWith(csrf()).put()
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

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = "1586";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_PathVariableInvalid_Returns400() {
        // Given
        String externalId = "undefined";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        // Given
        String externalId = "nonExisting";

        // Then
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(3)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = "1001";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_PathVariableError_Returns400() {
        // Given
        String nullExternalId = "null";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(nullExternalId);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + nullExternalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        // Given
        String externalId = "1001";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntExistsWithComplytId_Returns400ConflictedData() {
        // Given
        String externalId = "1002";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId).withComplytId(UUID.randomUUID());

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingSource_Returns400ConflictedData() {
        // Given
        String externalId = "1002";
        String differentSource = "9";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + differentSource + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_ConflictingExternalId_Returns400ConflictedData() {
        // Given
        String externalId = "someId";
        String differentExternalId = "differentId";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId);

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + differentExternalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Order(2)
    @Test
    @Override
    @WithMockJwt
    public void upsertByExternalIdAndSource_DoesntPassValidation_Returns400CValidationError() {
        // Given
        String externalId = "1003";
        CustomerDto customerDto = ITUtilities.stubCustomerDto(externalId)
                .withCustomerType(null).withName(null);
        Set<String> expectedErrors = Set.of(
                "customerType " + DtoErrorMessages.NOT_NULL_ERROR
        );

        // Then
        webTestClient
                .mutateWith(csrf()).put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(customerDto)
                .exchange()

                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    String[] errors = message.substring(1, message.length() - 1).split(", ");
                    assertEquals(expectedErrors.size(), errors.length);
                    for (String err : errors) {
                        assertTrue(expectedErrors.contains(err));
                    }
                });
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        int size = 5;
        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .hasSize(size);
    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        int page = 2;
        int size = 1;
        String expectedComplyId = "9ff0912a-2d60-4e8a-a6ba-1a9e7385338e";

        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL) // Set your API endpoint
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(customers -> Assertions.assertEquals(customers.get(0).complytId().toString(), expectedComplyId));

    }

    @Order(0)
    @Test
    @Override
    @WithMockJwt
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        int size = 1;
        String expectedComplyId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5";
        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("size", size)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(customers -> Assertions.assertEquals(customers.get(0).complytId().toString(), expectedComplyId))
                .hasSize(size);
    }

    @Order(0)
    @Test
    @WithMockJwt
    public void getAll_SortedByExternalTimestampsCreatedDate_ReturnsSortedEntries() {
        webTestClient
                .mutateWith(csrf()).get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(customers -> {
                    LocalDateTime lastDate = null;
                    for (CustomerDto customer : customers) {
                        LocalDateTime currentDate = LocalDateTime.parse(customer.externalTimestamps().createdDate());
                        if (lastDate != null) {
                            Assertions.assertTrue(currentDate.isBefore(lastDate) || currentDate.isEqual(lastDate),
                                    "Customer should be sorted by creation date in descending order");
                        }
                        lastDate = currentDate;
                    }
                });
    }


    @Order(1)
    @Override
    @Test
    @WithMockJwt
    public void patch_PatchesOneField_ReturnsPatchedResource() {
        String expectedComplyId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5";
        OptionalAddressDto addressToPatch = ITUtilities.createOptionalAddressDtoInCalifornia().withStreet("10010 Patch Street");

        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("address", addressToPatch);
        }};

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + expectedComplyId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> Assertions.assertEquals(returnedCustomer.address(), addressToPatch));
    }

    @Order(1)
    @Test
    @WithMockJwt
    public void patch_PatchesTwoFields_ReturnsPatchedResource() {
        String expectedComplyId = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5";
        OptionalAddressDto addressToPatch = ITUtilities.createOptionalAddressDtoInCalifornia().withStreet("10010 Patch Street");
        String nameToPatch = "nameToPatch";

        LinkedHashMap<String, Object> map = new LinkedHashMap<>() {{
            put("address", addressToPatch);
            put("name", nameToPatch);
        }};

        webTestClient
                .mutateWith(csrf()).patch()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/complytId/" + expectedComplyId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(map)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(returnedCustomer -> {
                    Assertions.assertEquals(returnedCustomer.address(), addressToPatch);
                    Assertions.assertEquals(returnedCustomer.name(), nameToPatch);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationSortedByDateDesc_ReturnsSortedList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                    LocalDateTime firstDate = LocalDateTime.parse(list.get(0).externalTimestamps().createdDate());
                    LocalDateTime secondDate = LocalDateTime.parse(list.get(1).externalTimestamps().createdDate());
                    LocalDateTime thirdDate = LocalDateTime.parse(list.get(2).externalTimestamps().createdDate());
                    LocalDateTime fourthDate = LocalDateTime.parse(list.get(3).externalTimestamps().createdDate());
                    assertTrue(firstDate.isAfter(secondDate));
                    assertTrue(secondDate.isAfter(thirdDate));
                    assertTrue(thirdDate.isAfter(fourthDate));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationSortedByDateAsc_ReturnsSortedList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("sortOrder", "asc")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                    LocalDateTime firstDate = LocalDateTime.parse(list.get(0).externalTimestamps().createdDate());
                    LocalDateTime secondDate = LocalDateTime.parse(list.get(1).externalTimestamps().createdDate());
                    LocalDateTime thirdDate = LocalDateTime.parse(list.get(2).externalTimestamps().createdDate());
                    LocalDateTime fourthDate = LocalDateTime.parse(list.get(3).externalTimestamps().createdDate());
                    assertTrue(firstDate.isBefore(secondDate));
                    assertTrue(secondDate.isBefore(thirdDate));
                    assertTrue(thirdDate.isBefore(fourthDate));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationSortedByNameDesc_ReturnsSortedCustomers() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("sortBy", "name")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                    String firstName = list.get(0).name();
                    String secondName = list.get(1).name();
                    String thirdName = list.get(2).name();
                    String fourthName = list.get(3).name();
                    assertTrue(firstName.compareTo(secondName) > 0);
                    assertTrue(secondName.compareTo(thirdName) > 0);
                    assertTrue(thirdName.compareTo(fourthName) > 0);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationSortedByNameAsc_ReturnsSortedCustomer() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("sortOrder", "asc")
                        .queryParam("sortBy", "name")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                    String firstName = list.get(0).name();
                    String secondName = list.get(1).name();
                    String thirdName = list.get(2).name();
                    String fourthName = list.get(3).name();
                    assertTrue(firstName.compareTo(secondName) < 0);
                    assertTrue(secondName.compareTo(thirdName) < 0);
                    assertTrue(thirdName.compareTo(fourthName) < 0);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationFilteredByMarketPlaceCustomerType_ReturnsCustomers() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("customerType", "MARKETPLACE")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    for (CustomerDto c : list) {
                        assertEquals(CustomerTypeDto.MARKETPLACE, c.customerType());
                    }
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationFilteredByRetailCustomerType_ReturnsCustomers() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("customerType", "RETAIL")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    for (CustomerDto c : list) {
                        assertEquals(CustomerTypeDto.RETAIL, c.customerType());
                    }
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PaginationFilteredByStateType_ReturnsCustomers() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("sortBy", "address.state")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                    String firstState = list.get(0).address().state();
                    String secondState = list.get(1).address().state();
                    String thirdState = list.get(2).address().state();
                    String fourthState = list.get(3).address().state();
                    assertTrue(firstState.compareTo(secondState) > 0);
                    assertTrue(secondState.compareTo(thirdState) > 0);
                    assertTrue(thirdState.compareTo(fourthState) > 0);
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "filtered_by_customer_type_and_state_pagination_tenant")
    public void getAll_PaginationFilteredByCustomerTypeAndState_ReturnsCustomers() {

        // Making sure that there are 4 customers when querying without filter
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> assertEquals(4, list.size()));

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("customerType", "MARKETPLACE")
                        .queryParam("address.state", "VA")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    for (CustomerDto c : list) {
                        assertEquals(CustomerTypeDto.MARKETPLACE, c.customerType());
                        assertEquals("VA", c.address().state());
                    }
                });
    }

    @Test
    @Override
    @WithMockJwt
    public void getAll_InvalidSortOrderSent_Throws400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("sortOrder", "ascc")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(GenericErrorMessages.INVALID_SORT_ORDER_PARAMETER));
                });
    }

    @Test
    @Override
    @WithMockJwt
    public void getAll_InvalidPageValuePassed_Throws400() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("page", "0")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(DtoErrorMessages.PAGE_FORMAT_ERROR));
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_PartialFilterValuePassed_ReturnsList() {

        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("customerType", "ETai")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(2, list.size());
                    for (CustomerDto c : list) {
                        assertEquals(CustomerTypeDto.RETAIL, c.customerType());
                    }
                });
    }

    @Test
    @Override
    @WithMockJwt(tenantId = "sorted_by_date_pagination_tenant")
    public void getAll_BlankFilterValuePassed_ReturnsFullList() {
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL)
                        .queryParam("customerType", "")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CustomerDto.class)
                .value(list -> {
                    assertEquals(4, list.size());
                });
    }

}