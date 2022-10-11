package com.complyt.v1.controllers;

import com.complyt.security.permissions.customer.ReadMessagesPermission;
import com.complyt.v1.model.Message;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "api", produces = MediaType.APPLICATION_JSON_VALUE)
// For simplicity of this sample, allow all origins. Real applications should configure CORS for their use case.
@CrossOrigin(origins = "*")
public class APIController {

    @GetMapping(value = "/public")
    public Mono<Message> publicEndpoint() {
        return Mono.just(new Message("All good. You DO NOT need to be authenticated to call /api/public."));
    }

    @GetMapping(value = "/private")
    public Mono<Message> privateEndpoint() {
        return Mono.just(new Message("All good. You can see this because you are Authenticated."));
    }

    @ReadMessagesPermission
    @GetMapping(value = "/private-scoped")
    public Mono<Message> privateScopedEndpoint() {
        return Mono.just(new Message("All good. You can see this because you are Authenticated with a Token granted the 'read:messages' scope"));
    }

    @ReadMessagesPermission
    @PreAuthorize("hasAuthority('SCOPE_read2:messages')")
    @GetMapping(value = "/private-scoped2")
    public Mono<Message> privateScopedEndpoint2() {
        return Mono.just(new Message("All good. You can see this because you are Authenticated with a Token granted the 'read:messages' scope"));
    }
}