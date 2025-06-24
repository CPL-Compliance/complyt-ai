package io.complyt.business.webhook.web_clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.utils.observability.ContextLogger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;

@Slf4j
public class WebhookWebClientWrapper<T extends ComplytIdProperty> extends WebClientWrapperBase implements WebClientWrapper<T> {

    private final String secretKey;

    public WebhookWebClientWrapper(WebClient webClient, String scheme, String host, String path, String secretKey) {
        super(webClient, scheme, host, path);
        this.secretKey = secretKey;
    }

    @Override
    public Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper) {
        URI uri = buildUri(this.scheme, webhookEntityWrapper.host(), webhookEntityWrapper.path());
        String hmaac;
        try {
            ObjectMapper objectMapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            hmaac = HmaacGenerator.generateHmacSHA256(secretKey, objectMapper.writeValueAsString(webhookEntityWrapper));
        } catch (Exception e) {
            return Mono.error(new RuntimeException("Failed to create Hmaac: " + e.getMessage(), e));
        }

        return ContextLogger.observeCtx("Sending webhook entity with request id: " + webhookEntityWrapper.id(), log::info)
                .then(webClient
                        .post()
                        .uri(uri)
                        .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                        .header("X-Signature", hmaac)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(webhookEntityWrapper)
                        .retrieve()
                        .bodyToMono(webhookEntityWrapper.getClass()))
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
                        .maxBackoff(Duration.ofSeconds(10))
                        .jitter(0.2)
                        .filter(throwable -> {
                            log.info("Retrying webhook due to error: {}", throwable.getMessage());
                            return true;
                        }))
                .doOnNext(res -> log.info("Sent webhook entity with request id: '{}", webhookEntityWrapper.id()))
                .thenReturn(webhookEntityWrapper.object()) // only called after successful completion
                .doOnError(err -> log.error("Webhook failed after retries: {}", err.getMessage()));
    }


//    @Override
//    public Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper) {
//        URI uri = buildUri(this.scheme, webhookEntityWrapper.host(), webhookEntityWrapper.path());
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
//                        .bodyToMono(webhookEntityWrapper.getClass()))
//                        .retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
//                                .maxBackoff(Duration.ofSeconds(10))
//                                .jitter(0.2)
//                                .filter(throwable -> {
//                                    log.info("Retrying webhook due to error: {}", throwable.getMessage());
//                                    return true; // You can filter only specific exceptions
//                                }))
//                        .doOnNext(res -> log.info("Sent webhook entity: '{}'", webhookEntityWrapper))
//                        .doOnError(err -> log.error("Webhook failed after retries: {}", err.getMessage()))
//                        .onErrorResume(err -> {
//                            log.error("Giving up on webhook for entity: {}", webhookEntityWrapper);
//                            return Mono.empty();
//                        })
//                        .thenReturn(webhookEntityWrapper.object());
//    }

}