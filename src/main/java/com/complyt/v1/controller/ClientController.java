package com.complyt.v1.controller;

import com.complyt.facade.ClientFacade;
import com.complyt.v1.model.ClientDto;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ClientController.BASE_URL)
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ClientController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String BASE_URL = "/v1/client";

    @Autowired
    ClientFacade clientFacade;

    @PostMapping("")
    public ClientDto createClient(@RequestBody @NotNull ClientDto clientDto){
        return clientFacade.createClient(clientDto);
    }

    @GetMapping("")
    public ClientDto getClientByName(@RequestParam String name){
        return clientFacade.getClient(name);
    }

}
