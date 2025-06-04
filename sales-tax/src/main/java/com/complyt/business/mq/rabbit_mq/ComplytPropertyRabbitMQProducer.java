package com.complyt.business.mq.rabbit_mq;


import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ComplytPropertyRabbitMQProducer<T extends ComplytIdProperty> {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routingKey.webhook.name}")
    private String webhookRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public ComplytPropertyRabbitMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendMessage(WebhookEntityWrapper<T> object) {
        log.info("send message: {}", object);
        rabbitTemplate.convertAndSend(exchange, webhookRoutingKey, object);
    }

}