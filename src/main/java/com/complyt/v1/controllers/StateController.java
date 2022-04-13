package com.complyt.v1.controllers;

import com.complyt.facades.StateFacade;
import com.complyt.v1.mappers.StateMapper;
import com.complyt.v1.model.StateDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(StateController.BASE_URL)
public class StateController {
    public static final String BASE_URL = "/v1/state";

    @NonNull
    private StateFacade stateFacade;

    @GetMapping("")
    public Mono<ResponseEntity<StateDto>> getState(@RequestParam String name) {
        return stateFacade.findByName(name)
                .map(customerItem -> new ResponseEntity<>(StateMapper.INSTANCE.stateToStateDto(customerItem), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND)).onErrorReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }
}