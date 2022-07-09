package com.complyt.v1.controllers;


import com.complyt.domain.Nexus;
import com.complyt.domain.State;
import com.complyt.facades.StateFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(StateController.class)
@WithMockUser(username = "mock", password = "mock")
public class StateControllerTest {

    @MockBean
    private StateFacade stateFacade;

    @Autowired
    private WebTestClient webTestClient;

    private final String NAME_PARAM = "name";

    @Test
    void getState_StateNotExists_ThrowsExceptionIsNotFound() throws Exception {
        // Given
        String stateNameNotExists = "Israel";
        when(stateFacade.findByName(stateNameNotExists)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, stateNameNotExists + " state Not Found"));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StateController.BASE_URL)
                        .queryParam(NAME_PARAM, stateNameNotExists)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
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

        Mono<State> state = Mono.just(new State(abbreviation, code, name));
        when(stateFacade.findByName(name)).thenReturn(state);

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(StateController.BASE_URL)
                        .queryParam(NAME_PARAM, name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(State.class)
                .value(stateItem -> stateItem.getName(), equalTo(name));
    }
}