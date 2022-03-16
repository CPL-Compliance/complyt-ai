package com.complyt.service;

import com.complyt.model.State;
import com.complyt.repository.StateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class StateServiceTest {
    @InjectMocks
    public StateService stateService;

    @Mock
    public StateRepository stateRepositoryMock;

    @Mock
    public State stateMock;

    @Test
    public void getState_ReturnedCalifornia_SearchingForCalifornia() {
        String expectedStateName = "California";
        String actualStateName = "California";
        when(stateMock.getName()).thenReturn(actualStateName);
        when(stateRepositoryMock.findByName(expectedStateName)).thenReturn(stateMock);

        State state = stateService.getState(actualStateName);

        assertEquals(expectedStateName, state.getName());
    }
}