package io.complyt.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class MQConfigTest {

    @Mock
    private ConnectionFactory connectionFactory;

    @InjectMocks
    private MQConfig mqConfig;

    @BeforeEach
    void setUp() throws Exception {
        Field queueField = MQConfig.class.getDeclaredField("queue");
        queueField.setAccessible(true);
        queueField.set(mqConfig, "test-queue");
    }

    @Test
    void testQueueBean() {
        Queue queue = mqConfig.queue();
        assertNotNull(queue);
        assertEquals("test-queue", queue.getName());
    }

    @Test
    void testMessageConverterBean() {
        MessageConverter converter = mqConfig.converter();
        assertNotNull(converter);
        assertInstanceOf(Jackson2JsonMessageConverter.class, converter);
    }

    @Test
    void testRabbitListenerContainerFactoryBean() {
        MessageConverter converter = mqConfig.converter();
        SimpleRabbitListenerContainerFactory factory =
                mqConfig.rabbitListenerContainerFactory(connectionFactory, converter);

        assertNotNull(factory);
    }
}
