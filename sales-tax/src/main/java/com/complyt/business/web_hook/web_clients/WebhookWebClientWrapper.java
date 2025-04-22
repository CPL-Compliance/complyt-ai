package com.complyt.business.web_hook.web_clients;

import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.utils.observability.ContextLogger;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

@Slf4j
@EqualsAndHashCode
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

    public Mono<T> sendWebhook(Class<T> resource, T object, String host, String path) {
        URI uri = buildUri(host, path);
        ContextLogger.observeCtx("Webhook object: " + object, log::info);
        ContextLogger.observeCtx("Webhook url: " + uri, log::info);

        return webClient
                .post()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(object), resource)
                .retrieve()
                .bodyToMono(resource)
                .retryWhen(Retry.backoff(5, Duration.ofMillis(50))
                        .onRetryExhaustedThrow(((retryBackoffSpec, retrySignal) ->
                                new RuntimeException(retrySignal.totalRetries() + " Retries Exhausted"))))
//                                ContextLogger.observeCtx("Failed to send a Webhook to url: " + uri, log::info))))
                .thenReturn(object);
    }

}