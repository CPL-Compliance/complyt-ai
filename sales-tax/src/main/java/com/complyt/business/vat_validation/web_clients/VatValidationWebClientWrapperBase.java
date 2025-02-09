package com.complyt.business.vat_validation.web_clients;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.springframework.web.reactive.function.client.WebClient;

@AllArgsConstructor
@ToString
public abstract class VatValidationWebClientWrapperBase implements VatValidationWebClientWrapper {
    protected final WebClient webClient;
    protected final String scheme;
    protected final String host;
    protected final String path;
}