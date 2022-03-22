package com.complyt.v1.controller;

import com.complyt.facade.StateFacade;
import com.complyt.v1.RestResponseEntityExceptionHandler;
import com.complyt.v1.exceptions.ResourceNotFoundException;
import com.complyt.v1.model.StateDto;
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

import static org.hamcrest.Matchers.equalTo;
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
        when(stateFacade.getStateByName(anyString())).thenThrow(ResourceNotFoundException.class);

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
        String stateName = "California";
        double salesTax = 0.6;
        StateDto stateDto = new StateDto(stateName, salesTax);
        when(stateFacade.getStateByName(stateName)).thenReturn(stateDto);

        // When
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get(StateController.BASE_URL)
                .param(NAME_PARAM, stateName)
                .contentType(MediaType.APPLICATION_JSON);

        // Then
        mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo(stateName)))
                .andExpect(jsonPath("$.salesTaxRate", equalTo(salesTax)));
    }
}