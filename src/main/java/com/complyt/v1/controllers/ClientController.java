package com.complyt.v1.controllers;

import com.complyt.domain.Client;
import com.complyt.facades.ClientFacade;
import com.complyt.v1.model.ClientDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(ClientController.BASE_URL)
public class ClientController {
    public static final String BASE_URL = "/v1/client";

    private ClientFacade clientFacade;

    @NonNull
    private ModelMapper modelMapper;

    @PostMapping("")
    public Mono<ClientDto> createClient(@RequestBody @NonNull ClientDto clientDto) {
        Client client = modelMapper.map(clientDto, Client.class);
        Mono<Client> clientMono = clientFacade.createClient(client);

        return clientMono.map(clientItem -> modelMapper.map(clientItem, ClientDto.class));
    }

    @GetMapping("")
    public Mono<ClientDto> getClientByName(@RequestParam String name) {
        Mono<Client> clientMono = clientFacade.findByName(name);

        return clientMono.map(clientItem -> modelMapper.map(clientItem, ClientDto.class));
    }
}
