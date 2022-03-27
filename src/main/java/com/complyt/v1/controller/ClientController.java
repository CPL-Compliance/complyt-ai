package com.complyt.v1.controller;

import com.complyt.facade.ClientFacade;
import com.complyt.v1.model.ClientDto;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
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
    public ClientDto createClient(@RequestBody @NotNull ClientDto clientDto){
        return clientFacade.createClient(clientDto);
    }

    @GetMapping("")
    public ClientDto getClientByName(@RequestParam String name){
        return clientFacade.getClient(name);
    }

}
