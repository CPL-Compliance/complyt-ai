package io.complyt.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.complyt.business.message_queue.rabbit_mq.WebhookEntityWrapperDeserializer;
import io.complyt.domain.audit.Action;
import io.complyt.domain.properties.ComplytIdProperty;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonDeserialize(using = WebhookEntityWrapperDeserializer.class)
public record WebhookEntityWrapper<T extends ComplytIdProperty>(UUID id, LocalDateTime timestamp, Action action,
                                                                String webhookClass, T object, String host,
                                                                String path) {

}