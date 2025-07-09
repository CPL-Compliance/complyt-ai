package com.complyt.business.message_queue.rabbit_mq;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.audit.Action;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookEntityWrapperMQProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    private WebhookEntityWrapperMQProducer<ComplytIdProperty> producer;

    private final String exchange = "test.exchange";
    private final String routingKey = "test.routing.key";

    @BeforeEach
    void setUp() {
        // Create a producer instance and manually inject the exchange and routing key
        producer = new WebhookEntityWrapperMQProducer<>(rabbitTemplate);
        producer.exchange = exchange;
        producer.routingJsonKey = routingKey;
    }

    @Test
    void sendMessage_shouldPublishMessageAndReturnMono() {
        // Arrange
        ComplytIdProperty mockPayload = mock(ComplytIdProperty.class);
        WebhookEntityWrapper<ComplytIdProperty> wrapper = new WebhookEntityWrapper<>(
                UUID.randomUUID(), LocalDateTime.now(), Action.CREATE, Transaction.class.getSimpleName(), mockPayload, "host", "path"
        );

        // Act
        Mono<WebhookEntityWrapper<ComplytIdProperty>> result = producer.sendMessage(wrapper);

        // Assert
        StepVerifier.create(result)
                .expectNext(wrapper)
                .verifyComplete();

    }
}
