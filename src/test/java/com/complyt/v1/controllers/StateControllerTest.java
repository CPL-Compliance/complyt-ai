package com.complyt.v1.controllers;


import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.facades.StateFacade;
import com.complyt.v1.exceptions.RestResponseEntityExceptionHandler;
import com.complyt.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StateControllerTest {

    @InjectMocks
    StateController stateController;

    @Mock
    StateFacade stateFacade;

    MockMvc mockMvc;

    private static final String NAME_PARAM = "name";

    @BeforeAll
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(stateController)
                .setControllerAdvice(new RestResponseEntityExceptionHandler())
                .build();
    }

    @Test
    void getState_StateNotExists_ThrowsExceptionIsNotFound() throws Exception {
        // Given
        String stateNameNotExists = "Israel";
        when(stateFacade.findByName(anyString())).thenThrow(ResourceNotFoundException.class);

        // When
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(StateController.BASE_URL).
                param(NAME_PARAM, stateNameNotExists).
                contentType(MediaType.APPLICATION_JSON);

        //Then
        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isNotFound());
    }

    @Test
    void getState_StateExists_StatusIsOkAndEquals() throws Exception {
        // Given
        String id = UUID.randomUUID().toString();
        double salesTaxRate = 0.6;
        String abbreviation = "Abbreviation";
        String code = "08";
        String name = "California";
        List<Nexus> nexuses = null;

        Mono<State> state = Mono.just(new State(id, salesTaxRate, abbreviation, code, name, nexuses));
        when(stateFacade.findByName(name)).thenReturn(state);

        // When
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(StateController.BASE_URL)
                .param(NAME_PARAM, name)
                .contentType(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(name)))
                .andExpect(jsonPath("$.salesTaxRate", equalTo(salesTaxRate)));
    }
}