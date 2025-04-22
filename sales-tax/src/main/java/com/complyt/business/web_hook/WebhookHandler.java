package com.complyt.business.web_hook;

import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
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
    private WebhookWebClientWrapper webhookWebClientWrapper;

    public Mono<T> handleWebhook(Class<T> resource, T obj) {
        return clientTrackingService.getClientTracking()
                .flatMap(clientTracking -> clientTracking.getShouldForwardWriteOperations() ?
                        webhookWebClientWrapper.sendWebhook(resource, obj, clientTracking.getHost(), clientTracking.getPath()) : null)
                .thenReturn(obj);
    }

//    private Mono<Boolean> shouldExecuteWebHook(ClientTracking clientTracking) {
//        return clientTracking.shouldForwardWriteOperations;
//    }

}
