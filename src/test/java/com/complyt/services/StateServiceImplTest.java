package com.complyt.services;

import com.complyt.domain.State;
import com.complyt.repositories.StateRepository;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class StateServiceImplTest {
    @InjectMocks
    StateServiceImpl stateServiceImpl;

    @Mock
    StateRepository stateRepositoryMock;

    @Mock
    State stateMock;

    @Test
    void getState_ReturnedCalifornia_SearchingForCalifornia() {
        String expectedStateName = "California";
        String actualStateName = "California";
        when(stateMock.getName()).thenReturn(actualStateName);
        when(stateRepositoryMock.findByName(expectedStateName)).thenReturn(stateMock);

        StateDto stateDto = stateServiceImpl.findByName(actualStateName);

        assertNotNull(stateDto);
        assertEquals(expectedStateName, stateDto.getName());
    }

    @Test
    void getState_ReturnedEmpty_SearchingForNotExistingState() {
        String expectedStateName = "California";
        String actualStateName = "California";
        when(stateMock.getName()).thenReturn(actualStateName);
        when(stateRepositoryMock.findByName(expectedStateName)).thenReturn(stateMock);

        StateDto stateDto = stateServiceImpl.findByName(actualStateName);

        assertEquals(expectedStateName, stateDto.getName());
    }
}