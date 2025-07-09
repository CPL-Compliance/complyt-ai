package com.complyt.domain;

import com.complyt.domain.audit.Action;
import com.complyt.domain.properties.ComplytIdProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookEntityWrapper<T extends ComplytIdProperty>(
        UUID id,
        LocalDateTime timestamp,
        Action action,
        String webhookClass,
        T object,
        String host,
        String path
) {
}
