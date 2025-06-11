package io.complyt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    @Value("${rabbitmq.queue.name}")
    private String queue;

//    @Value("${rabbitmq.exchange.name}")
//    private String exchange;
//
//    @Value("${rabbitmq.routing.key}")
//    private String routingKey;

    @Bean
    public Queue queue() {
        return new Queue(queue);
    }

//    @Bean
//    public TopicExchange exchange() {
//        return new TopicExchange(exchange);
//    }

//    @Bean
//    public Binding binding() {
//        return BindingBuilder
//                .bind(queue())
//                .to(exchange())
//                .with(routingKey);
//    }

    @Bean
    public MessageConverter converter() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        System.out.println(">>> Custom ObjectMapper for RabbitMQ is ACTIVE!");
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    //    @Bean(name = "rabbitListenerContainerFactory")
//    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory,
//                                                                               MessageConverter messageConverter) {
//        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setMessageConverter(messageConverter);
//        System.out.println(">>> Custom SimpleRabbitListenerContainerFactory for RabbitMQ is ACTIVE!");
//        return factory;
//    }
    @Bean(name = "rabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }

}