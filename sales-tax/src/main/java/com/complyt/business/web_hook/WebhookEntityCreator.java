package com.complyt.business.web_hook;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.audit.Action;
import com.complyt.domain.properties.ComplytIdProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class WebhookEntityCreator<T extends ComplytIdProperty> {

    public Mono<WebhookEntityWrapper<T>> create(Class<T> webhookClass, T object, Action action) {
        LocalDateTime timestamp = LocalDateTime.now();
        UUID id = UUID.randomUUID();
        WebhookEntityWrapper<T> webhookEntityWrapper = new WebhookEntityWrapper<>(id, timestamp,action, webhookClass, object);

        return Mono.just(webhookEntityWrapper);
    }

}