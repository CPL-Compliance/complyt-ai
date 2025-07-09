package com.complyt.business.web_clients;

import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@AllArgsConstructor
public abstract class WebClientWrapperBase {
    protected  WebClient webClient;
    protected  String scheme;
    protected  String host;
    protected  String path;

    protected URI buildUri(String scheme, String host, String path) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .build()
                .toUri();
    }
}
