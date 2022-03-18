package com.complyt.service;

import com.complyt.domain.Order;
import com.complyt.repository.OrderRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepositoryTested;

    @Mock
    Order orderMock;

    @Test
    void save() {
    }

    @Test
    void testSave() {
    }

    @Test
    void getOrderById() {
        String id = UUID.randomUUID().toString();

        Mockito.when(orderMock.getId()).thenReturn(id);
        Mockito.when(orderRepositoryTested.findById(id)).thenReturn(orderMock);

        Order order = orderService.getOrderById(id);

        Assertions.assertEquals(id, order.getId());
    }
}