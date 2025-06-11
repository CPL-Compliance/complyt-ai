package io.complyt.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.complyt.domain.audit.Action;
import io.complyt.domain.properties.ComplytIdProperty;

import java.time.LocalDateTime;
import java.util.UUID;

public record WebhookEntityWrapper<T extends ComplytIdProperty>(
        UUID id,
        LocalDateTime timestamp,
        Action action,
        String webhookClass,
        JsonNode object
) {


    public <T extends ComplytIdProperty> T getObjectAs(Class<T> clazz) throws JsonProcessingException {
        return new ObjectMapper().treeToValue(object, clazz);
    }
}
