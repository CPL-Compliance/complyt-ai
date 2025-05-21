package com.complyt.business.web_hook.web_clients;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import reactor.core.publisher.Mono;

public interface WebClientWrapper<T extends ComplytIdProperty> {
    Mono<T> sendWebhook(WebhookEntityWrapper<T> webhookEntityWrapper, String host, String path);
}