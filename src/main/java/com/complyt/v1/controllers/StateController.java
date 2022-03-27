package com.complyt.v1.controllers;

import com.complyt.facades.StateFacade;
import com.complyt.v1.exceptions.ResourceNotFoundException;
import com.complyt.v1.model.StateDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@AllArgsConstructor
@RestController
@RequestMapping(StateController.BASE_URL)
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class StateController {
    public static final String BASE_URL = "/v1/state";

    private StateFacade stateFacade;

    @GetMapping("")
    public StateDto getState(@RequestParam String name) {
        try {
            StateDto stateDto = stateFacade.findByName(name);

            return stateDto;
        } catch (ResourceNotFoundException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, name + " state Not Found", exc);
        }
    }
}
