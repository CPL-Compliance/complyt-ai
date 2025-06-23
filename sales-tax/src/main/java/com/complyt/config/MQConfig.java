package com.complyt.config;

import com.complyt.business.message_queue.rabbit_mq.StubProducer;
import com.complyt.business.message_queue.rabbit_mq.WebhookEntityWrapperMQProducer;
import com.complyt.domain.properties.ComplytIdProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class MQConfig {

    @Profile({"demo", "production"})
    public MessageConverter converter() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    @Profile({"demo", "production"})
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }


    @Bean
    @Profile({"demo", "production"})
    public <T extends ComplytIdProperty> WebhookEntityWrapperMQProducer<T> webhookProducer(
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.exchange.name}") String exchange,
            @Value("${rabbitmq.routing.key}") String routingJsonKey) {

        return new WebhookEntityWrapperMQProducer<>(rabbitTemplate, exchange, routingJsonKey);
    }

    @Bean
    @Profile({"default", "integration-test"})
    public <T extends ComplytIdProperty> StubProducer<T> stubProducer() {
        return new StubProducer<>();
    }

}