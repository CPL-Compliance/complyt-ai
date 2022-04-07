package com.complyt.v1.controllers;

import com.complyt.domain.State;
import com.complyt.facades.StateFacade;
import com.complyt.v1.exceptions.ResourceNotFoundException;
import com.complyt.v1.mappers.StateMapper;
import com.complyt.v1.model.StateDto;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(StateController.BASE_URL)
public class StateController {
    public static final String BASE_URL = "/v1/state";

    private StateFacade stateFacade;

    private ModelMapper modelMapper;

    @GetMapping("")
    public Mono<StateDto> getState(@RequestParam String name) {
        try {
            Mono<State> stateMono = stateFacade.findByName(name);

            return stateMono.map(stateItem -> modelMapper.map(stateItem, StateDto.class));
        } catch (ResourceNotFoundException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, name + " state Not Found", exc);
        }
    }
}