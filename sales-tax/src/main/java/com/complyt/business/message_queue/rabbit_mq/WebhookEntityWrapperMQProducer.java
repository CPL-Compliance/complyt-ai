package com.complyt.business.message_queue.rabbit_mq;

import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.properties.ComplytIdProperty;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

@Slf4j
@AllArgsConstructor
public class WebhookEntityWrapperMQProducer<T extends ComplytIdProperty> implements MQProducer<T> {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    String exchange;

    @Value("${rabbitmq.routing.key}")
    String routingJsonKey;

    public WebhookEntityWrapperMQProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public Mono<WebhookEntityWrapper<T>> sendMessage(WebhookEntityWrapper<T> webhookEntityWrapper) {
        rabbitTemplate.convertAndSend(exchange, routingJsonKey, webhookEntityWrapper);
        log.info(String.format("Message sent -> %s", webhookEntityWrapper));
        return Mono.just(webhookEntityWrapper);
    }

}