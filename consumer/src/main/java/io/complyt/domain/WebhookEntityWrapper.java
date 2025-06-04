package io.complyt.domain;


import io.complyt.domain.audit.Action;
import io.complyt.domain.properties.ComplytIdProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookEntityWrapper<T extends ComplytIdProperty>(
        UUID id,
        LocalDateTime timestamp,
        Action action,
        Class<T> webhookClass,
        T object
) {
}
