package com.complyt.v1.controllers;

import com.complyt.domain.Client;
import com.complyt.facades.ClientFacade;
import com.complyt.v1.mappers.ClientMapper;
import com.complyt.v1.model.ClientDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

@Tag(name = "Client", description = "This is the Client controller")
@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(ClientController.BASE_URL)
public class ClientController {
    public static final String BASE_URL = "/v1/client";

    private final ClientFacade clientFacade;

    @Operation(summary = "Creates client")
    @PostMapping("")
    public ClientDto createClient(@RequestBody @NonNull ClientDto clientDto) {
        Client client = ClientMapper.INSTANCE.clientDtoToClient(clientDto);
        Client createdClient = clientFacade.createClient(client);

        return ClientMapper.INSTANCE.clientToClientDto(createdClient);
    }

    @Operation(summary = "Gets client by name")
    @GetMapping("")
    public Mono<ClientDto> getClientByName(@RequestParam String name) {
        Mono<Client> clientMono = clientFacade.findByName(name);

        return clientMono
                .map(clientItem -> ClientMapper.INSTANCE.clientToClientDto(clientItem))
                .switchIfEmpty(Mono.error(new NotFoundException(name)));
    }
}