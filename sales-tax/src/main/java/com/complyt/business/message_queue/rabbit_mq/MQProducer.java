package com.complyt.business.message_queue.rabbit_mq;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import reactor.core.publisher.Mono;

public interface MQProducer<T extends ComplytIdProperty> {
    Mono<WebhookEntityWrapper<T>> sendMessage(WebhookEntityWrapper<T> webhookEntityWrapper);
}
