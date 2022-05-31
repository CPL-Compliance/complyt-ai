package com.complyt.v1.controllers;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.facades.OrderFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@WebFluxTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WithMockUser(username = "mock", password = "mock")
class OrderControllerTest {

    @MockBean
    private OrderFacade orderFacade;

    @Autowired
    private WebTestClient webTestClient;

    Order orderWithId;

    OrderDto orderDto;

    @BeforeEach
    void cleanUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f)
                    ));
            }
        };

        orderWithId = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, OrderStatus.ACTIVE);
        orderDto = OrderMapper.INSTANCE.orderToOrderDto(orderWithId);
    }

    @Test
    void initController_NullFacadeInstanceGiven_ThrowsNullPointerException() {
        // Given
        OrderFacade facade = null;
        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderController controller = new OrderController(facade);
        });

        assertEquals(nullPointerException.getMessage(), "orderFacade is marked non-null but is null");
    }

    @Test
    void update_NewOrderCreated_SavesOrder() {
        // Given
        Order orderNoId = orderWithId.withId(null);
        String externalId = orderNoId.getExternalId();
        when(orderFacade.upsert(externalId, orderNoId)).thenReturn(Mono.just(orderWithId));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(orderDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .value(orderDtoItem -> orderDtoItem, equalTo(orderDto));
    }

    @Test
    void update_UpdateFails_Returns5xxServerError() {
        // Given
        String externalId = orderWithId.getExternalId();
        when(orderFacade.update(externalId, orderWithId)).thenThrow(OperationFailedException.class);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(orderWithId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getByExternalId_FindsOrder_ReturnsOrder() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.just(orderWithId));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .value(orderItem -> orderItem, equalTo(orderDto));
    }

    @Test
    void getByExternalId_OperationFails_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAll_ExpectTwoOrders_ReturnsTwoOrders() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        OrderDto orderNoId = orderDto.withExternalId(firstId);
        OrderDto secondOrderNoId = orderDto.withExternalId(secondId);
        Order firstOrder = orderWithId.withExternalId(firstId);
        Order secondOrder = orderWithId.withExternalId(secondId);
        List<OrderDto> allOrdersWithNoId = new ArrayList<OrderDto>() {{
            add(orderNoId);
            add(secondOrderNoId);
        }};

        // When
        when(orderFacade.getAll()).thenReturn(Flux.just(firstOrder, secondOrder));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderDto.class)
                .value(orderDtos -> orderDtos, equalTo(allOrdersWithNoId));
    }

    @Test
    void updateSalesTax_UpdatesOrder_ReturnsStatus200() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Order order = orderWithId.withExternalId(externalId);

        // When + Then
        when(orderFacade.updateSalesTax(orderWithId.getExternalId())).thenReturn(Mono.just(orderWithId));
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + orderWithId.getExternalId() + "/salesTax")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .value(orderDto -> orderDto, equalTo(orderDto));

    }

    @Test
    void markAsCancelled_CancelsOrder_OrderStatusChanges() {
        // Given
        Order cancelledOrdered = orderWithId.withOrderStatus(OrderStatus.CANCELLED);

        // When + Then
        when(orderFacade.markAsCancelled(orderWithId.getExternalId())).thenReturn(Mono.just(cancelledOrdered));
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + orderWithId.getExternalId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();

    }
}

