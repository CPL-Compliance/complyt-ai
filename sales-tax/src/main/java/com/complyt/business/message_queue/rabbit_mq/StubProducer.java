package com.complyt.business.message_queue.rabbit_mq;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import reactor.core.publisher.Mono;

public class StubProducer<T extends ComplytIdProperty> implements MQProducer<T> {

    @Override
    public Mono<WebhookEntityWrapper<T>> sendMessage(WebhookEntityWrapper<T> webhookEntityWrapper) {
        return Mono.just(webhookEntityWrapper);
    }
}
