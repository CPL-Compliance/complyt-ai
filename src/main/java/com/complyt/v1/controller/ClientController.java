package com.complyt.v1.controller;

import com.complyt.domain.Client;
import com.complyt.facade.ClientFacade;
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
    public Client createClient(@RequestBody @NotNull Client client){
        return clientFacade.createClient(client);
    }

    @GetMapping("")
    public Client getClientByName(@RequestParam String name){
        return clientFacade.getClient(name);
    }

}
