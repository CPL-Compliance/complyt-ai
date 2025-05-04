package com.complyt.business.web_hook.web_clients;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.utils.observability.ContextLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
public class WebhookWebClientWrapper<T extends ComplytIdProperty> extends WebClientWrapperBase {

    public WebhookWebClientWrapper(WebClient webClient, String scheme, String host, String path) {
        super(webClient, scheme, host, path);
    }

    private URI buildUri(String host, String path) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .build()
                .toUri();
    }

    public Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper, String host, String path) {
        URI uri = buildUri(host, path);

        return ContextLogger.observeCtx("Sending webhook entity: " + webhookEntityWrapper, log::info)
                .then(webClient
                        .post()
                        .uri(uri)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(webhookEntityWrapper, WebhookEntityWrapper.class)
                        .retrieve()
                        .bodyToMono(webhookEntityWrapper.webhookClass())
                        .onErrorResume(err -> ContextLogger.observeCtx(
                                        "Failed to send webhook to external API. Webhook details: " + webhookEntityWrapper +
                                                ", Error: " + err.getMessage(), log::error)
                                .thenReturn(webhookEntityWrapper.object()))
                        .thenReturn(webhookEntityWrapper.object()));
    }

}