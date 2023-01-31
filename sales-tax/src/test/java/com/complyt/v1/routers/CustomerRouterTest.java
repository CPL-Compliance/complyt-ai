package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.config.JacksonConfig;
import com.complyt.domain.customer.Customer;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.CustomerDtoCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebFluxTest
@Import(JacksonConfig.class)
@WithMockUser(username = "mock", password = "mock")
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

    @BeforeEach
    void setUp() {
        customerDto = CustomerDtoCreator.create();
        customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
    }

    @Test
    void upsert_NewCustomerCreated_SavesCustomer() {
        // Given
        String externalId = customerDto.getExternalId();
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));
        when(customerDtoValidationHandler.validate(any())).thenReturn(Mono.just(customerDto));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .equals(customerDto);
    }

    @Test
    void accessNonExistingPath_NotFound() {
        // Given
        String externalId = customerDto.getExternalId();
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.empty());
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
    void upsert_CustomerExists_UpdatesCustomer() {
        // Given
        String externalId = customer.getExternalId();
        Customer newCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Customer originalCustomer = newCustomer.withName("originalCustomer");
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.just(originalCustomer));
        when(customerFacade.saveCustomer(newCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(newCustomer, originalCustomer)).thenReturn(Mono.just(newCustomer));
        when(customerDtoValidationHandler.validate(any())).thenReturn(Mono.just(customerDto));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(customerDto);
    }

    @Test
    void getByExternalId_FindsCustomer_ReturnsCustomer() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .equals(customerDto);
    }

    @Test
    void update_UpdateFails_Returns5xxServerError() {
        // Given

        String externalId = customer.getExternalId();
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(customer)).thenThrow(OperationFailedException.class);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerRouter.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getByExternalId_OperationFails_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(CustomerRouter.BASE_URL + "/" + externalId).build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
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
    void getAll_AllCustomersRetrieved_ReturnsAllCustomersFound() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id);

        List<Customer> allCustomers = new ArrayList<>() {{
            add(customer);
            add(secondCustomer);
        }};

        when(customerFacade.getAllCustomers()).thenReturn(Flux.fromIterable(allCustomers));

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

//    @Test
//    void upsertCustomer_NullExternalId_ThrowsNullPointerException() {
//        // Given
//        String nullExternalId = null;
//        customerRouter = new CustomerController(customerFacade);
//
//        // When
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            customerRouter.upsert(nullExternalId, customerDto);
//        });
//
//        // Then
//        assertEquals("externalId is marked non-null but is null", nullPointerException.getMessage());
//    }
//
//    @Test
//    void upsertCustomer_NullCustomerDto_ThrowsNullPointerException() {
//        //Given
//        String externalId = UUID.randomUUID().toString();
//        CustomerDto nullCustomerDto = null;
//        customerRouter = new CustomerController(customerFacade);
//
//        // When
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            customerRouter.upsert(externalId, nullCustomerDto);
//        });
//
//        // Then
//        assertEquals("customerDto is marked non-null but is null", nullPointerException.getMessage());
//    }
//
//    @Test
//    void getByExternalId_NullExternalId_ThrowsNullPointerException() {
//        //Given
//        String nullExternalId = null;
//        customerRouter = new CustomerController(customerFacade);
//
//        // When
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            customerRouter.getByExternalId(nullExternalId);
//        });
//
//        // Then
//        assertEquals("externalId is marked non-null but is null", nullPointerException.getMessage());
//    }
//
//    @Test
//    void getByName_NullName_ThrowsNullPointerException() {
//        //Given
//        String nullName = null;
//        customerRouter = new CustomerController(customerFacade);
//
//        // When
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            customerRouter.getByName(nullName);
//        });
//
//        // Then
//        assertEquals("name is marked non-null but is null", nullPointerException.getMessage());
//    }
}