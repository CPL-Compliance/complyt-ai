package com.complyt.v1.controllers;

import com.complyt.domain.Client;
import com.complyt.facades.ClientFacade;
import com.complyt.v1.mappers.ClientMapper;
import com.complyt.v1.model.ClientDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(ClientController.BASE_URL)
public class ClientController {
    public static final String BASE_URL = "/v1/client";

    private ClientFacade clientFacade;

    @PostMapping("")
    public ClientDto createClient(@RequestBody @NonNull ClientDto clientDto) {
        Client client = ClientMapper.INSTANCE.clientDtoToClient(clientDto);
        Client createdClient = clientFacade.createClient(client);
        log.debug("This is a testy");
        return ClientMapper.INSTANCE.clientToClientDto(createdClient);
    }

    @GetMapping("")
    public ClientDto getClientByName(@RequestParam String name) {
        Client client = clientFacade.findByName(name);
        return ClientMapper.INSTANCE.clientToClientDto(client);
    }
}
