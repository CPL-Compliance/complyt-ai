package com.complyt.v1.controllers;

import com.complyt.domain.Client;
import com.complyt.facades.ClientFacade;
import com.complyt.v1.mappers.ClientMapper;
import com.complyt.v1.model.ClientDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping(ClientController.BASE_URL)
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ClientController {
    //private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String BASE_URL = "/v1/client";

    private ClientFacade clientFacade;

    @PostMapping("")
    public ClientDto createClient(@RequestBody @NonNull ClientDto clientDto) {
        Client client = ClientMapper.INSTANCE.clientDtoToClient(clientDto);
        Client createdClient = clientFacade.createClient(client);

        return ClientMapper.INSTANCE.clientToClientDto(createdClient);
    }

    @GetMapping("")
    public ClientDto getClientByName(@RequestParam String name) {
        Client client = clientFacade.findByName(name);
        return ClientMapper.INSTANCE.clientToClientDto(client);
    }
}
