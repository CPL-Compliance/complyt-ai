package integration.services.sales_tax;

import integration.TestContainersInitializerIT;
import integration.test_utils.TestUtilities;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClientTrackingEndpointsIT extends TestContainersInitializerIT implements ClientTrackingEndpointsITTemplate{

    @Order(2)
    @Test
    @Override
    public void getAll_Exists_Returns200() {
        // Given & Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk();
    }

    @Order(2)
    @Test
    @Override
    public void getAll_QueryParamInvalid_Returns400() {
        // Given & Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .queryParam("size", TestUtilities.NULL_STRING)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Order(2)
    @Test
    @Override
    public void getByAll_DoesntExists_Returns200EmptyList() {
        // Given - Doing it by setting page that does not exist
        int page = 5;

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .queryParam("page", page)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(0);
    }


    @Order(2)
    @Test
    @Override
    public void getAll_GetByParamSize_ReturnsExpectedSize() {
        // Given
        int size = 1;

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .queryParam("size", size)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectBodyList(String.class)
                .value(list ->
                        assertEquals(1, list.size()));
    }


    @Order(2)
    @Test
    @Override
    public void getAll_GetByParamPage_ReturnsExpectedPage() {
        // Given
        int size = 1;
        int page = 2;
        String expectedTenantId = "it_tenant";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .queryParam("size", size)
                        .queryParam("page", page)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectBodyList(LinkedHashMap.class)
                .value(customer -> assertEquals(customer.get(0).get("tenantId"), expectedTenantId));
    }


    @Order(2)
    @Test
    @Override
    public void getAll_GetByDefaultsSizeAndPage_ReturnsExpectedEntries() {
        // Given
        String expectedTenantId = "other_it_tenant";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectBodyList(LinkedHashMap.class)
                .value(customer -> assertEquals(customer.get(0).get("tenantId"), expectedTenantId));
    }


    @Order(2)
    @Test
    @Override
    public void getByName_Exists_Returns200() {
        // Given
        String name = "RAZ";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/name/" + name)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectBodyList(LinkedHashMap.class)
                .value(customer -> assertEquals(customer.get(0).get("name"), name));
    }


    @Order(2)
    @Test
    @Override
    public void getByName_DoesntExists_Returns404() {
        // Given
        String name = "nameNotInDB";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/name/" + name)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }

    @Order(2)
    @Test
    @Override
    public void getByName_PathVariableInvalid_Returns400() {
        // Given
        String name = TestUtilities.stringWithLength(257);

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/name/" + name)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Order(2)
    @Test
    @Override
    public void getByTenantId_Exists_Returns200() {
        // Given
        String tenantId = "org_12345";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(LinkedHashMap.class)
                .value(customer -> assertEquals(customer.get(0).get("tenantId"), tenantId));
    }


    @Order(2)
    @Test
    @Override
    public void getByTenantId_NotExists_Returns404() {
        // Given
        String tenantId = "org_notFound";

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isNotFound();
    }


    @Order(2)
    @Test
    @Override
    public void getByTenantId_PathVariableInvalid_Returns400() {
        // Given
        String tenantId = TestUtilities.stringWithLength(257);

        // Then
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Order(2)
    @Test
    @Override
    public void upsertByTenantId_Exists_Returns200() {
        // Given: This tenant already in DB
        String tenantId = "org_12345";
        String name = "RAZ";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.clientTrackingJsonExample(name, tenantId))
                .exchange()
                .expectStatus().isOk();
    }
    
    
    @Order(2)
    @Test
    @Override
    public void upsertByTenantId_PathVariableInvalid_Returns400() {
        // Given
        String tenantId = TestUtilities.stringWithLength(257);
        String name = "RAZ";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.clientTrackingJsonExample(name, tenantId))
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Order(2)
    @Test
    @Override
    public void upsertByTenantId_ConflictedTenantId_Returns400() {
        // Given
        String tenantId = "org_12345";
        String name = "RAZ";
        String otherTenantId = "org_otherTenant";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.clientTrackingJsonExample(name, otherTenantId))
                .exchange()
                .expectStatus().isBadRequest();
    }


    @Order(1)
    @Test
    @Override
    public void upsertByTenantId_DoesntExists_Returns201() {
        // Given
        String tenantId = "org_12345";
        String name = "RAZ";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue(TestUtilities.clientTrackingJsonExample(name, tenantId))
                .exchange()
                .expectStatus().isCreated();
    }


    @Order(2)
    @Test
    @Override
    public void upsertByTenantId_UnsupportedMediaType_Returns415() {
        // Given
        String tenantId = "org_12345";

        // Then
        WEB_TEST_CLIENT
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .headers(headers -> {
                    headers.setBearerAuth(TOKEN_COMPLYT_ADMIN);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                })
                .bodyValue("Unsupported data")
                .exchange()
                .expectStatus().is4xxClientError();
    }


    @Order(2)
    @Test
    @Override
    public void get_NoAccessToken_Returns401() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Order(2)
    @Test
    @Override
    public void get_InsufficientScopes_Returns403() {
        WEB_TEST_CLIENT
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TestUtilities.CLIENT_TRACKING_BASE_URL)
                        .build())
                .headers(headers -> headers.setBearerAuth(TOKEN))
                .exchange()
                .expectStatus().isForbidden();
    }
}
