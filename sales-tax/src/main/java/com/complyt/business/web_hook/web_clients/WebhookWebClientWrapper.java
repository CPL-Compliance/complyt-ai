package com.complyt.business.web_hook.web_clients;

import com.complyt.annotations.Generated;
import com.complyt.business.web_hook.HmaacGenerator;
import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.utils.observability.ContextLogger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Generated
@Slf4j
public class WebhookWebClientWrapper<T extends ComplytIdProperty> extends WebClientWrapperBase implements WebClientWrapper<T> {

    private final String secretKey;

    public WebhookWebClientWrapper(WebClient webClient, String scheme, String host, String path, String secretKey) {
        super(webClient, scheme, host, path);
        this.secretKey = secretKey;
    }

    private URI buildUri(String host, String path) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .build()
                .toUri();
    }

    @Override
    public Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper, String host, String path) {
        URI uri = buildUri(host, path);
        String hmaac;
        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            hmaac = HmaacGenerator.generateHmacSHA256(secretKey, objectMapper.writeValueAsString(webhookEntityWrapper));
        } catch (Exception e) {
            return ContextLogger.observeCtx("Failed to create Hmaac.  Error: " + e.getMessage(), log::error)
                    .thenReturn(webhookEntityWrapper.object());
        }

        return ContextLogger.observeCtx("Sending webhook entity: " + webhookEntityWrapper, log::info)
                .then(webClient
                        .post()
                        .uri(uri)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .header("X-Signature", hmaac)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(webhookEntityWrapper)
                        .retrieve()
                        .bodyToMono(webhookEntityWrapper.webhookClass())
                        .onErrorResume(err -> ContextLogger.observeCtx(
                                        "Failed to send webhook to external API.  Error: " + err.getMessage(), log::error)
                                .thenReturn(webhookEntityWrapper.object()))
                        .thenReturn(webhookEntityWrapper.object()));
    }

}