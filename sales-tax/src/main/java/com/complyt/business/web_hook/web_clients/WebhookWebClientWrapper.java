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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

@Generated
@Slf4j
public class WebhookWebClientWrapper<T extends ComplytIdProperty> extends WebClientWrapperBase implements WebClientWrapper<T> {

    private final String secretKey;

    public WebhookWebClientWrapper(WebClient webClient, String scheme, String host, String path, String secretKey) {
        super(webClient, scheme, host, path);
        this.secretKey = secretKey;
    }

//    @Override
//    public Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper, String host, String path) {
//        URI uri = buildUri(this.scheme, host, path);
//        String hmaac;
//        try {
//            ObjectMapper objectMapper = new ObjectMapper()
//                    .registerModule(new JavaTimeModule())
//                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            hmaac = HmaacGenerator.generateHmacSHA256(secretKey, objectMapper.writeValueAsString(webhookEntityWrapper));
//        } catch (Exception e) {
//            return ContextLogger.observeCtx("Failed to create Hmaac.  Error: " + e.getMessage(), log::error)
//                    .thenReturn(webhookEntityWrapper.object());
//        }
//        return ContextLogger.observeCtx("Sending webhook entity: " + webhookEntityWrapper, log::info)
//                .then(webClient
//                        .post()
//                        .uri(uri)
//                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                        .header("X-Signature", hmaac)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(webhookEntityWrapper)
//                        .retrieve()
//                        .bodyToMono(webhookEntityWrapper.webhookClass())
//                        .onErrorResume(err -> ContextLogger.observeCtx(
//                                        "Failed to send webhook to external API.  Error: " + err.getMessage(), log::error)
//                                .thenReturn(webhookEntityWrapper.object()))
//                        .thenReturn(webhookEntityWrapper.object()));
//    }

//    @Override
//    public Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper, String host, String path) {
//        URI uri = buildUri(this.scheme, host, path);
//        String hmaac;
//        try {
//            ObjectMapper objectMapper = new ObjectMapper()
//                    .registerModule(new JavaTimeModule())
//                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//            hmaac = HmaacGenerator.generateHmacSHA256(secretKey, objectMapper.writeValueAsString(webhookEntityWrapper));
//        } catch (Exception e) {
//            return ContextLogger.observeCtx("Failed to create Hmaac.  Error: " + e.getMessage(), log::error)
//                    .thenReturn(webhookEntityWrapper.object());
//        }
//        return ContextLogger.observeCtx("Sending webhook entity: " + webhookEntityWrapper, log::info)
//                .then(webClient
//                        .post()
//                        .uri(uri)
//                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//                        .header("X-Signature", hmaac)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .bodyValue(webhookEntityWrapper)
//                        .retrieve()
//                        .bodyToMono(webhookEntityWrapper.webhookClass())
//                        .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
//                                .maxBackoff(Duration.ofSeconds(10))
//                                .jitter(0.2)
//                                .filter(throwable -> {
//                                    log.warn("Retrying webhook due to error: {}", throwable.getMessage());
//                                    return true; // You can filter only specific exceptions
//                                }))
//                        .doOnNext(res -> log.info("Sent webhook entity: '{}'", webhookEntityWrapper))
//                        .doOnError(err -> log.error("Webhook failed after retries: {}", err.getMessage()))
//                        .onErrorResume(err -> {
//                            log.error("Giving up on webhook for entity: {}", webhookEntityWrapper);
//                            return Mono.empty();
//                        })
//                        .thenReturn(webhookEntityWrapper.object()));
//    }

    @Override
    public Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper, String host, String path) {

        Mono.defer(() -> {
                    URI uri = buildUri(this.scheme, host, path);
                    String hmaac;
                    try {
                        ObjectMapper objectMapper = new ObjectMapper()
                                .registerModule(new JavaTimeModule())
                                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                        String json = objectMapper.writeValueAsString(webhookEntityWrapper);
                        hmaac = HmaacGenerator.generateHmacSHA256(secretKey, json);
                        log.info("JSON: '{}' , hmaac: '{}'", json, hmaac);
                    } catch (Exception e) {
                        return ContextLogger.observeCtx("Failed to create Hmaac.  Error: " + e.getMessage(), log::error)
                                .thenReturn(webhookEntityWrapper.object());
                    }

                    return webClient.post()
                            .uri(uri)
                            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                            .header("X-Signature", hmaac)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(webhookEntityWrapper)
                            .retrieve()
                            .bodyToMono(webhookEntityWrapper.webhookClass())
                            .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                                    .maxBackoff(Duration.ofSeconds(10))
                                    .jitter(0.2)
                                    .filter(throwable -> {
                                        log.warn("Retrying webhook due to error: {}", throwable.getMessage());
                                        return true; // You can filter only specific exceptions
                                    }))
                            .doOnNext(res -> log.info("Sent webhook entity: '{}'", webhookEntityWrapper))
                            .doOnError(err -> log.error("Webhook failed after retries: {}", err.getMessage()))
                            .onErrorResume(err -> {
                                log.error("Giving up on webhook for entity: {}", webhookEntityWrapper);
                                return Mono.empty();
                            });
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();

        return Mono.just(webhookEntityWrapper.object()); // Return immediately
    }

}