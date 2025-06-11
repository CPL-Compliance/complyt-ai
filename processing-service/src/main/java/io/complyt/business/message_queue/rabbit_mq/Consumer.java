package io.complyt.business.message_queue.rabbit_mq;

import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.properties.ComplytIdProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static io.complyt.domain.ClassMapper.CLASS_MAP;

@Slf4j
@Service
public class Consumer<T extends ComplytIdProperty> {

    @RabbitListener(queues = {"${rabbitmq.queue.name}"}, containerFactory = "rabbitListenerContainerFactory")
    public void consume(WebhookEntityWrapper wrapper) {
        try {
            String simpleClassName = wrapper.webhookClass();
            Class<? extends ComplytIdProperty> clazz = CLASS_MAP.get(simpleClassName);

            if (clazz == null) {
                throw new IllegalArgumentException("Unknown class: " + simpleClassName);
            }

            T typedObject = (T) wrapper.getObjectAs(clazz);

            // You can now use obj.getComplytId(), etc.
            handleMessage(wrapper);

        } catch (Exception e) {
            // handle/log error
            log.error("Error consuming message: ", e);
        }
    }

    private void handleMessage(WebhookEntityWrapper obj) {
        // business logic using T
        log.info("Processing entity: {}", obj);
    }
}