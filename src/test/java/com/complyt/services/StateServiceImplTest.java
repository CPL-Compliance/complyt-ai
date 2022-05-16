package com.complyt.services;

import com.complyt.domain.State;
import com.complyt.repositories.StateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class StateServiceImplTest {
    @InjectMocks
    StateServiceImpl stateServiceImpl;

    @Mock
    StateRepository stateRepositoryMock;

    State state;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        state = new State(UUID.randomUUID().toString(),
                1d,
                "abbreviation",
                "code",
                "state",
                new ArrayList<>());
    }

    @Test
    void getState_ReturnedCalifornia_SearchingForCalifornia() {
        // Given
        String expectedStateName = "California";
        State stateUnderTest = state.withName(expectedStateName);
        when(stateRepositoryMock.findOneByName(expectedStateName)).thenReturn(Mono.just(stateUnderTest));

        // When
        Mono<State> monoState = stateServiceImpl.findOneByName(expectedStateName);

        // Then
        StepVerifier.create(monoState).expectNext(stateUnderTest).verifyComplete();
    }

    @Test
    void getState_ReturnedEmpty_SearchingForNotExistingState() {
        // Given
        String stateName = "non existing state";
        when(stateRepositoryMock.findOneByName(stateName)).thenReturn(Mono.empty());

        // When
        Mono<State> monoState = stateServiceImpl.findOneByName(stateName);

        // Then
        StepVerifier.create(monoState).expectNextCount(0).verifyComplete();
    }
}