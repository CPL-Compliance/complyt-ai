package com.complyt.business.message_queue.rabbit_mq;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.audit.Action;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

class StubProducerTest {

    static class DummyComplytIdProperty implements ComplytIdProperty {
        @Override
        public UUID getComplytId() {
            return null;
        }

        @Override
        public ComplytIdProperty withComplytId(UUID complytId) {
            return null;
        }
    }

    @Test
    void testSendMessageReturnsMonoWithSameEntity() {
        // Given
        StubProducer<DummyComplytIdProperty> stubProducer = new StubProducer<>();
        DummyComplytIdProperty dummyObject = new DummyComplytIdProperty();
        WebhookEntityWrapper<DummyComplytIdProperty> wrapper = new WebhookEntityWrapper<>(
                UUID.randomUUID(), LocalDateTime.now(), Action.CREATE, Transaction.class.getSimpleName(), dummyObject, "host", "path"
        );

        // When
        Mono<WebhookEntityWrapper<DummyComplytIdProperty>> result = stubProducer.sendMessage(wrapper);

        // Then
        StepVerifier.create(result)
                .expectNext(wrapper)
                .verifyComplete();
    }
}
