package com.complyt.v1.routers;

import com.complyt.domain.ClientTracking;
import com.complyt.facades.ClientTrackingFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.config.ApiExceptionConfig;
import com.complyt.v1.config.ValidatorConfig;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.GenericErrorMessages;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handlers.ClientTrackingHandler;
import com.complyt.v1.mappers.ClientTrackingMapper;
import com.complyt.v1.models.ClientTrackingDtoTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest
@ContextConfiguration(classes = {ClientTrackingRouter.class, ClientTrackingHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class ClientTrackingRouterTest implements ClientTrackingRouterTestTemplate {

    ClientTracking clientTracking;
    ClientTrackingDtoTenant ClientTrackingDtoTenant;
    UnitTestUtilities testUtilities;
    List<ClientTracking> clientTrackingList;
    List<ClientTracking> emptyClientTrackingList;
    String name;
    String tenantId;

    @Autowired
    ClientTrackingRouter clientTrackingRouter;

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ClientTrackingFacade clientTrackingFacade;

    @BeforeEach
    void setUp() {
        name = "name";
        tenantId = "org_nD6T71fMDbR0qTSY";
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        ClientTrackingDtoTenant = testUtilities.createClientTrackingDtoTenant(tenantId);
        clientTracking = ClientTrackingMapper.INSTANCE.ClientTrackingDtoTenantToClientTracking(ClientTrackingDtoTenant);
        ClientTracking secondClientTracking = clientTracking.withId(UUID.randomUUID().toString());
        clientTrackingList = new ArrayList<>() {{
            add(clientTracking);
            add(secondClientTracking);
        }};
        emptyClientTrackingList = new ArrayList<>();
    }


    @Override
    @Test
    @WithMockUser
    public void getAll_Exists_Returns200WithList() {
        // When
        when(clientTrackingFacade.getAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(clientTrackingList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(clientTrackingList);
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_QueryParamInvalid_Returns400() {
        // When
        when(clientTrackingFacade.getAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(clientTrackingList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .queryParam("page", "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("page has to be numeric"));
                });
    }

    @Override
    public void getAll_UserWithoutAuthorities_Returns403() {
        //???
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_EmptyCollection_Returns200WithEmptyList() {
        // When
        when(clientTrackingFacade.getAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(emptyClientTrackingList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(emptyClientTrackingList);
    }

    @Override
    @Test
    public void getAll_UnauthenticatedUser_Returns401() {
        // When
        when(clientTrackingFacade.getAll(0, RepositoryConstant.DEFAULT_PAGE_SIZE)).thenReturn(Flux.fromIterable(emptyClientTrackingList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_InternalServerError_Returns500() {
        // When
        when(clientTrackingFacade.getAll(0, 0)).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_NullHandler_ThrowsNullPointerException() {
        ClientTrackingHandler nullClientTrackingHandler = null;
        ClientTrackingRouter clientTrackingRouter = new ClientTrackingRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            clientTrackingRouter.getAll(nullClientTrackingHandler);
        });

        // Then
        assertEquals("clientTrackingHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void getByName_Exists_Returns200WithList() {
        // WHen
        when(clientTrackingFacade.getByName(name)).thenReturn(Flux.fromIterable(clientTrackingList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(clientTrackingList);
    }

    @Override
    @Test
    public void getByName_UnauthenticatedUser_Returns401() {
        // WHen
        when(clientTrackingFacade.getByName(name)).thenReturn(Flux.fromIterable(clientTrackingList));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Override
    @Test
    @WithMockUser
    public void getByName_InternalServerError_Returns500() {
        // When
        when(clientTrackingFacade.getByName(name)).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void getByName_NullHandler_ThrowsNullPointerException() {
        ClientTrackingHandler nullClientTrackingHandler = null;
        ClientTrackingRouter clientTrackingRouter = new ClientTrackingRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            clientTrackingRouter.getByName(nullClientTrackingHandler);
        });

        // Then
        assertEquals("clientTrackingHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void getByTenantId_Exists_Returns200WithList() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(clientTracking);
    }

    @Override
    @Test
    @WithMockUser
    public void getByTenantId_PathVariableInvalid_Returns400() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(GenericErrorMessages.TENANT_ID_FORMAT));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void getByTenantId_UnauthenticatedUser_Returns401() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Override
    @Test
    @WithMockUser
    public void getByTenantId_InternalServerError_Returns500() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void getByTenantId_NullHandler_ThrowsNullPointerException() {
        ClientTrackingHandler nullClientTrackingHandler = null;
        ClientTrackingRouter clientTrackingRouter = new ClientTrackingRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            clientTrackingRouter.getByTenantId(nullClientTrackingHandler);
        });

        // Then
        assertEquals("clientTrackingHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_Exists_Returns200() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.just(clientTracking));
        when(clientTrackingFacade.updateIfModified(clientTracking, clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));
        when(clientTrackingFacade.saveClientTracking(clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ClientTrackingDtoTenant)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(ClientTrackingDtoTenant);
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_PathVariableInvalid_Returns400() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.just(clientTracking));
        when(clientTrackingFacade.updateIfModified(clientTracking, clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));
        when(clientTrackingFacade.saveClientTracking(clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + "null")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(ClientTrackingDtoTenant)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(GenericErrorMessages.TENANT_ID_FORMAT));
                });
    }



    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_BlankNexus_Returns400ValidationError() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                {
                    "name": "SKY",
                    "tenantId": "org_nD6T71fMDbR0qTSY"
                }
                """)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("must not be null"));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_BlankName_Returns400ValidationError() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                {
                    "nexus": {
                        "taxableDate": "2015-06-01T00:00:00"
                    },
                    "tenantId": "org_nD6T71fMDbR0qTSY"
                }
                """)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("must not be null"));
                });
    }

    @Test
    @WithMockUser
    public void upsertByTenantId_BlankTenantId_Returns400ValidationError() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                {
                    "nexus": {
                        "taxableDate": "2015-06-01T00:00:00"
                    },
                    "name": "SKY"
                }
                """)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("must not be null"));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_LengthGreaterThen256Name_Returns400ValidationError() {
        String name = testUtilities.stringWithLength(257);

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   " +
                        "    \"nexus\": {\n" +
                        "        \"taxableDate\": \"2015-06-01T00:00:00\"\n" +
                        "    },\n" +
                        "    \"name\": \"" + name + "\",\n" +
                        "    \"tenantId\": \"org_12345\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains("ClientTracking.name should be up to 256 characters maximum"));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_DifferentTenantIdInBody_Returns400ConflictedData() {
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                {
                    "nexus": {
                        "taxableDate": "2015-06-01T00:00:00"
                    },
                    "name": "SKY",
                    "tenantId": "org_12345"
                }
                """)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = map.get("message").toString();
                    assertTrue(message.contains(GenericErrorMessages.TENANT_ID_FORMAT));
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_DoesntExists_Returns201() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.empty());
        when(clientTrackingFacade.updateIfModified(clientTracking, clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));
        when(clientTrackingFacade.saveClientTracking(clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(ClientTrackingDtoTenant)
                .exchange()
                .expectStatus().isCreated()
                .equals(ClientTrackingDtoTenant);
    }

    @Override
    @Test
    public void upsertByTenantId_UnauthenticatedUser_Returns401() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.empty());
        when(clientTrackingFacade.updateIfModified(clientTracking, clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));
        when(clientTrackingFacade.saveClientTracking(clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(ClientTrackingDtoTenant)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_InternalServerError_Returns500() {
        // When
        when(clientTrackingFacade.updateIfModified(clientTracking, clientTrackingList.get(1), tenantId)).thenReturn(Mono.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_NullHandler_ThrowsNullPointerException() {
        ClientTrackingHandler nullClientTrackingHandler = null;
        ClientTrackingRouter clientTrackingRouter = new ClientTrackingRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            clientTrackingRouter.upsert(nullClientTrackingHandler);
        });

        // Then
        assertEquals("clientTrackingHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByTenantId_UnsupportedMediaType_Returns415() {
        // When
        when(clientTrackingFacade.getByTenantId(tenantId)).thenReturn(Mono.empty());
        when(clientTrackingFacade.updateIfModified(clientTracking, clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));
        when(clientTrackingFacade.saveClientTracking(clientTracking, tenantId)).thenReturn(Mono.just(clientTracking));

        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(ClientTrackingRouter.BASE_URL + "/tenantId/" + tenantId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue("Unsupported data")
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(LinkedHashMap.class)
                .value(map -> assertEquals(GenericErrorMessages.UNSUPPORTED_MEDIA_TYPE, map.get("message")));
    }

}