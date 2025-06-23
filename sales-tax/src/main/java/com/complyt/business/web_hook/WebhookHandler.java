package com.complyt.business.web_hook;

import com.complyt.business.message_queue.rabbit_mq.MQProducer;
import com.complyt.business.message_queue.rabbit_mq.WebhookEntityWrapperMQProducer;
import com.complyt.domain.WebhookDetails;
import com.complyt.domain.audit.Action;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
@Slf4j
public class WebhookHandler<T extends ComplytIdProperty> {

    @NonNull
    MQProducer<T> webhookEntityWrapperMQProducer;

    @NonNull
    private WebhookEntityCreator<T> webhookEntityCreator;

    public Mono<T> handleWebhook(Class<T> webhookClass, T object, WebhookDetails webhookDetails, Action action) {
        return shouldForwardRequest(webhookDetails)
                .flatMap(should -> should ?
                        webhookEntityCreator.create(webhookClass.getSimpleName(), object, action, webhookDetails.host(), webhookDetails.path())
                                .flatMap(webhookEntityWrapper -> webhookEntityWrapperMQProducer.sendMessage(webhookEntityWrapper)) : Mono.empty())
                .thenReturn(object);
    }

    private Mono<Boolean> shouldForwardRequest(WebhookDetails webhookDetails) {
        Boolean shouldForward = webhookDetails != null && webhookDetails.shouldForwardWriteOperations();
        return ContextLogger.observeCtx("Checking whether webhook forwarding is required returned " + shouldForward, log::info)
                .thenReturn(shouldForward);
    }
}