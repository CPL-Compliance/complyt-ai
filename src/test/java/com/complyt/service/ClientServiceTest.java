package com.complyt.service;

import com.complyt.domain.State;
import com.complyt.repository.ClientRepository;
import com.complyt.repository.StateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @InjectMocks
    public ClientService clientService;

    @Mock
    ClientRepository clientRepositoryMock;

    @Test
    void save() {
    }

    @Test
    void getClient() {
        
    }

    @Test
    void addOrderToClient() {
    }
}