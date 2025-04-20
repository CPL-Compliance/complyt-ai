package com.complyt.business;

import org.springframework.web.reactive.function.client.WebClient;

public abstract class WebClientWrapperBase {
    protected final WebClient webClient;
    protected final String scheme;
    protected final String host;
    protected final String path;
}
