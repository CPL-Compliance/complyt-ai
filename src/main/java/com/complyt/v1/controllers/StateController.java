package com.complyt.v1.controllers;

import com.complyt.facades.StateFacade;
import com.complyt.security.permissions.state.StateReadPermission;
import com.complyt.v1.mappers.StateMapper;
import com.complyt.v1.model.StateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "State", description = "This is the State controller")
@AllArgsConstructor
@RestController
@RequestMapping(StateController.BASE_URL)
public class StateController {
    public static final String BASE_URL = "/v1/state";

    @NonNull
    private final StateFacade stateFacade;

    @Operation(summary = "Gets state by name")
    @StateReadPermission
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<StateDto>> getState(@NonNull @RequestParam String name) {
        return stateFacade.findByName(name)
                .map(customerItem -> new ResponseEntity<>(StateMapper.INSTANCE.stateToStateDto(customerItem), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
                .onErrorReturn(new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Operation(summary = "Test method")
    @StateReadPermission
    @GetMapping("/test")
    @ResponseStatus(HttpStatus.OK)
    public Mono<String> test(){
        return Mono.just("Hi from Docker :)");
    }
}