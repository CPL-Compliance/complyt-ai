package io.complyt.business.message_queue.rabbit_mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.audit.Action;
import io.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class WebhookEntityWrapperDeserializerTest {

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void shouldDeserializeTransactionSuccessfully() throws IOException {
        String json = """
            {
              "id": "123e4567-e89b-12d3-a456-426614174000",
              "timestamp": "2025-06-24T12:00:00",
              "action": "CREATE",
              "webhookClass": "Transaction",
              "object": {
              "complytId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
              "id": "txn001"
              },
              "host": "example.com",
              "path": "/webhook"
            }
        """;

        var parser = mapper.getFactory().createParser(json);
        var deserializer = new WebhookEntityWrapperDeserializer<Transaction>();
        WebhookEntityWrapper<Transaction> result = deserializer.deserialize(parser, mapper.getDeserializationContext());

        assertEquals(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), result.id());
        assertEquals(LocalDateTime.parse("2025-06-24T12:00:00"), result.timestamp());
        assertEquals(Action.CREATE, result.action());
        assertEquals("Transaction", result.webhookClass());
        assertEquals("example.com", result.host());
        assertEquals("/webhook", result.path());
        assertNotNull(result.object());
        assertEquals("txn001", result.object().id());
    }

    @Test
    void shouldThrowExceptionForUnsupportedWebhookClass() {
        String json = """
            {
              "id": "123e4567-e89b-12d3-a456-426614174000",
              "timestamp": "2025-06-24T12:00:00",
              "action": "CREATE",
              "webhookClass": "UnknownClass",
              "object": {},
              "host": "example.com",
              "path": "/webhook"
            }
        """;

        assertThrows(IllegalArgumentException.class, () -> {
            var parser = mapper.getFactory().createParser(json);
            var deserializer = new WebhookEntityWrapperDeserializer<>();
            deserializer.deserialize(parser, mapper.getDeserializationContext());
        });
    }
}
