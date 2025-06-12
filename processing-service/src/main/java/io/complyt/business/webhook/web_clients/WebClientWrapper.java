package io.complyt.business.webhook.web_clients;

import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.properties.ComplytIdProperty;
import reactor.core.publisher.Mono;

public interface WebClientWrapper<T extends ComplytIdProperty> {
    Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper);
}