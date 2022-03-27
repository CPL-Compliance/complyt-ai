package com.complyt.v1.controllers;

import com.complyt.facades.ClientFacade;
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
        return clientFacade.createClient(clientDto);
    }

    @GetMapping("")
    public ClientDto getClientByName(@RequestParam String name, Model model) {
        return clientFacade.findByName(name);
    }
}
