package com.complyt.v1.controllers;

import com.complyt.domain.State;
import com.complyt.facades.StateFacade;
import com.complyt.v1.exceptions.ResourceNotFoundException;
import com.complyt.v1.mappers.StateMapper;
import com.complyt.v1.model.StateDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@RestController
@RequestMapping(StateController.BASE_URL)
public class StateController {
    public static final String BASE_URL = "/v1/state";

    private StateFacade stateFacade;

    @GetMapping("")
    public StateDto getState(@RequestParam String name) {
        try {
            State state = stateFacade.findByName(name);

            return StateMapper.INSTANCE.stateToStateDto(state);
        } catch (ResourceNotFoundException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, name + " state Not Found", exc);
        }
    }
}
