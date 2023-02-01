package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.handlers.CustomerHandler;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.validators.ValidationHandler;
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
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


@WebFluxTest
@ContextConfiguration(classes = {CustomerRouter.class, CustomerHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
class CustomerRouterTest {
    Customer customer;

    CustomerDto customerDto;

    @Autowired
    CustomerRouter customerRouter;

    @MockBean
    private ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler;

    @MockBean
    private CustomerFacade customerFacade;

    @Autowired
    private WebTestClient webTestClient;

    private ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        ObjectStub objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customerDto = objectStub.createCustomerDto(UUID.randomUUID().toString());
        customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
    }

    @Test
    @WithMockUser()
    void upsert_NewCustomerCreated_SavesCustomer() {
        // Given®
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));
        when(customerDtoValidationHandler.validate(any())).thenReturn(Mono.just(customerDto));

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
                .expectStatus().isCreated()
                .equals(customerDto);
    }

    @Test
    @WithMockUser()
    void accessNonExistingPath_NotFound() {
        // Given
        String externalId = customerDto.externalId();
        String source = customerDto.source();
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));
        when(customerDtoValidationHandler.validate(any())).thenReturn(Mono.just(customerDto));

        // When + Then
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

    @Test
    @WithMockUser()
    void upsert_CustomerExists_UpdatesCustomer() {
        // Given
        String externalId = customer.getExternalId();
        String source = customerDto.source();
        Customer newCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Customer originalCustomer = newCustomer.withName("originalCustomer");
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(originalCustomer));
        when(customerFacade.saveCustomer(newCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(newCustomer, originalCustomer)).thenReturn(Mono.just(newCustomer));
        when(customerDtoValidationHandler.validate(any())).thenReturn(Mono.just(customerDto));

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
                .expectStatus().isOk()
                .equals(customerDto);
    }

    @Test
    @WithMockUser
    void getByExternalId_FindsCustomer_ReturnsCustomer() {
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
    @WithMockUser
    void getByComplytId_FindsCustomer_ReturnsCustomer() {
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
    @WithUserDetails
    void update_UpdateFails_Returns5xxServerError() {
        // Given

        String externalId = customer.getExternalId();
        String source = customerDto.source();
        when(customerFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(customer)).thenThrow(OperationFailedException.class);

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
                .expectStatus().is5xxServerError();
    }

    @Test
    @WithUserDetails
    void getByExternalId_OperationFails_Returns4xxNotFound() {
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

    @Test
    @WithUserDetails
    void getByComplytId_OperationFails_Returns4xxNotFound() {
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

    @Test
    @WithUserDetails
    void getByName_FindsCustomer_ReturnsCustomer() {
        // Given
        String name = "name";
        List<Customer> customersFoundByName = new ArrayList<>() {{
            add(customer);
        }};
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(customersFoundByName));

        // When + Then
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

    @Test
    @WithUserDetails
    void getAll_AllCustomersRetrieved_ReturnsAllCustomersFound() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id);

        List<Customer> allCustomers = new ArrayList<>() {{
            add(customer);
            add(secondCustomer);
        }};

        when(customerFacade.getAll()).thenReturn(Flux.fromIterable(allCustomers));

        // When + Then
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
    @WithUserDetails
    void getAllBySource_AllCustomersRetrieved_ReturnsAllCustomersFound() {
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

    @Test
    void getAllCustomersRouterFunctions_NullHandler_ThrowsException() {
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
    void getCustomerByExternalIdRouterFunctions_NullHandler_ThrowsException() {
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
    void getAllCustomersBySourceRouterFunctions_NullHandler_ThrowsException() {
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
    void getCustomerByComplytIdRouterFunctions_NullHandler_ThrowsException() {
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
    void getCustomerByNameRouterFunctions_NullHandler_ThrowsException() {
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
    void upsertCustomerByExternalIdRouterFunctions_NullHandler_ThrowsException() {
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
}