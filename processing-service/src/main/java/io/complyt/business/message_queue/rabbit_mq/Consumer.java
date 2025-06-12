package io.complyt.business.message_queue.rabbit_mq;

import io.complyt.business.webhook.web_clients.WebClientWrapper;
import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.nexus.SalesTaxTracking;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.transaction.Transaction;
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

    @RabbitListener(queues = "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
    public Mono<Void> consume(WebhookEntityWrapper<T> wrapper) {
        ComplytIdProperty obj = wrapper.object();

        if (obj instanceof Transaction) {
            System.out.println(">>>>> Received Transaction");
        } else if (obj instanceof SalesTaxTracking) {
            System.out.println(">>>>> Received SalesTaxTracking");
        }

        return webhookWebClientWrapper
                .sendWebhook(wrapper) // Returns Mono<T>
                .then();              // Convert to Mono<Void>
    }

}


//    @RabbitListener(queues = "${rabbitmq.queue.name}", containerFactory = "rabbitListenerContainerFactory")
//    public Mono<Void> consume(WebhookEntityWrapper<T> wrapper) {
//        ComplytIdProperty obj = wrapper.getObject();
//
//        if (obj instanceof Transaction tx) {
//            System.out.println("tx");
//        } else if (obj instanceof SalesTaxTracking stt) {
//            System.out.println("stt");
//        }
//
//        return webClientWrapper.sendWebhook(wrapper).then();
//    }

//    @RabbitListener(queues = {"${rabbitmq.queue.name}"}, containerFactory = "rabbitListenerContainerFactory")
//    public void consume(WebhookEntityWrapper<T> webhookEntityWrapper) {
//        try {
//            String simpleClassName = webhookEntityWrapper.webhookClass();
//            Class<? extends ComplytIdProperty> clazz = CLASS_MAP.get(simpleClassName);
//
//            if (clazz == null) {
//                throw new IllegalArgumentException("Unknown class: " + simpleClassName);
//            }
//
//            handleMessage(webhookEntityWrapper);
//
//        } catch (Exception e) {
//            log.error("Error consuming message: ", e);
//        }
//    }
//
//    private void handleMessage(WebhookEntityWrapper<T> webhookEntityWrapper) {
//        log.info("Processing entity: {}", webhookEntityWrapper);
//    }