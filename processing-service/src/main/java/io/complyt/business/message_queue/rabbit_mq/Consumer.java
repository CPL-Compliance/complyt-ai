package io.complyt.business.message_queue.rabbit_mq;

import io.complyt.business.webhook.web_clients.WebClientWrapper;
import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.properties.ComplytIdProperty;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@AllArgsConstructor
public class Consumer<T extends ComplytIdProperty> {

    @NonNull
    WebClientWrapper<T> webhookWebClientWrapper;

    @RabbitListener(queues = "${rabbitmq-queue-name}", containerFactory = "rabbitListenerContainerFactory")
    public Mono<Void> consume(WebhookEntityWrapper<T> wrapper) {
        log.info("Message Received: " + wrapper);

        return webhookWebClientWrapper
                .sendWebhook(wrapper)
                .then();
    }

}