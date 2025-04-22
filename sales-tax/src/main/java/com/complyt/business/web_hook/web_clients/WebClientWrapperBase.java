package com.complyt.business.web_hook.web_clients;

import lombok.AllArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
public abstract class WebClientWrapperBase {
    protected  WebClient webClient;
    protected  String scheme;
    protected  String host;
    protected  String path;
}
