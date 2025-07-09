package com.complyt.config;

import com.complyt.business.message_queue.rabbit_mq.StubProducer;
import com.complyt.business.message_queue.rabbit_mq.WebhookEntityWrapperMQProducer;
import com.complyt.domain.properties.ComplytIdProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MQConfigTest {

    MQConfig mqConfig = new MQConfig();

    @Mock
    ConnectionFactory connectionFactory;

    @Mock
    RabbitTemplate rabbitTemplate;

    @Test
    void testConverterReturnsJacksonConverter() {
        MessageConverter converter = mqConfig.converter();
        assertNotNull(converter);
    }

    @Test
    void testRabbitTemplateCreation() {
        RabbitTemplate template = mqConfig.rabbitTemplate(connectionFactory);
        assertNotNull(template);
        assertNotNull(template.getMessageConverter());
    }

    @Test
    void testWebhookProducerCreation() {
        WebhookEntityWrapperMQProducer<ComplytIdProperty> producer = mqConfig.webhookProducer(
                rabbitTemplate, "test-exchange", "test-routing"
        );
        assertNotNull(producer);
    }

    @Test
    void testStubProducerCreation() {
        StubProducer<ComplytIdProperty> stubProducer = mqConfig.stubProducer();
        assertNotNull(stubProducer);
    }
}
