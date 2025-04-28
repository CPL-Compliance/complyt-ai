package com.complyt.business.web_hook;

import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.services.ClientTrackingService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class WebhookHandler<T extends ComplytIdProperty> {

    @NonNull
    private ClientTrackingService clientTrackingService;

    @NonNull
    private WebhookWebClientWrapper<T> webhookWebClientWrapper;

    @NonNull
    private WebhookEntityCreator<T> webhookEntityCreator;

    public Mono<T> handleWebhook(Class<T> webhookClass, T object) {

        return clientTrackingService.getClientTracking()
                .flatMap(clientTracking -> shouldForwardRequest(clientTracking) ?
                        webhookEntityCreator.create(webhookClass, object)
                                .flatMap(webhookEntityWrapper ->
                                        webhookWebClientWrapper.sendWebhook(webhookEntityWrapper, clientTracking.getWebhookDetails().host(), clientTracking.getWebhookDetails().path())) : Mono.empty())
                .thenReturn(object);
    }

    private boolean shouldForwardRequest(ClientTracking clientTracking) {
        return clientTracking.getWebhookDetails() != null && clientTracking.getWebhookDetails().shouldForwardWriteOperations();
    }

}
