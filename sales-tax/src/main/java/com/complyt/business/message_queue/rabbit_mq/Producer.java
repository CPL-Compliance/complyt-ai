package com.complyt.business.message_queue.rabbit_mq;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class Producer<T extends ComplytIdProperty> {

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    @Value("${rabbitmq.routing.key}")
    String routingJsonKey;

    private final RabbitTemplate rabbitTemplate;

    public Producer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public Mono<WebhookEntityWrapper<T>> sendMessage(WebhookEntityWrapper<T> webhookEntityWrapper) {
        rabbitTemplate.convertAndSend(exchange, routingJsonKey, webhookEntityWrapper);
        log.info(String.format("Message sent -> %s", webhookEntityWrapper));
        return Mono.just(webhookEntityWrapper);
    }

}