package io.complyt.business.message_queue.rabbit_mq;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.audit.Action;
import io.complyt.domain.nexus.SalesTaxTracking;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.transaction.Transaction;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public class WebhookEntityWrapperDeserializer<T extends ComplytIdProperty> extends JsonDeserializer<WebhookEntityWrapper<T>> {

    private static final Map<String, Class<? extends ComplytIdProperty>> TYPE_MAP = Map.of(
            "Transaction", Transaction.class,
            "SalesTaxTracking", SalesTaxTracking.class
    );

    @Override
    public WebhookEntityWrapper<T> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode root = codec.readTree(p);

        UUID id = UUID.fromString(root.get("id").asText());
        LocalDateTime timestamp = LocalDateTime.parse(root.get("timestamp").asText());
        String action = root.get("action").asText(); // optional: map to enum
        String webhookClass = root.get("webhookClass").asText();
        JsonNode objectNode = root.get("object");
        String host = root.get("host").asText();
        String path = root.get("path").asText();

        Class<? extends ComplytIdProperty> clazz = TYPE_MAP.get(webhookClass);
        if (clazz == null) {
            throw new IllegalArgumentException("Unsupported webhookClass: " + webhookClass);
        }

        ComplytIdProperty object = codec.treeToValue(objectNode, clazz);

        return new WebhookEntityWrapper<>(id, timestamp, String.valueOf(Action.valueOf(action)), webhookClass, (T) object, host, path);
    }
}
