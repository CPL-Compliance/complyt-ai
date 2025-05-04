package com.complyt.business.webhook.web_clients;

import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
import com.complyt.config.WebClientWrapperProperties;
import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WebhookWebClientWrapperTest {

    @InjectMocks
    WebhookWebClientWrapper<Transaction> webhookWebClientWrapper;

    @Mock
    WebClient webClient;

    @Mock
    WebClientWrapperProperties webhookWebClientWrapperProperties;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    static MockedStatic mockedStatic;

    UnitTestUtilities testUtilities;
    Transaction transaction;
    WebhookEntityWrapper<Transaction> webhookEntityWrapper;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        webhookEntityWrapper = testUtilities.createWebhookEntityWrapper();
    }

    @Test
    void sendWebhook_SendsWebhook() {
        // Given + When
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class), eq(WebhookEntityWrapper.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(webhookEntityWrapper.webhookClass())).thenReturn(Mono.just(webhookEntityWrapper.object()));

        Mono<Transaction> transactionMono = webhookWebClientWrapper.sendWebhook(webhookEntityWrapper, "host", "path");

        // Then
        StepVerifier.create(transactionMono).expectNext(webhookEntityWrapper.object()).verifyComplete();
    }

    @Test
    void sendWebhook_ErrorReturned_RequestFails() {

        // Given + When
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(Object.class), eq(WebhookEntityWrapper.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(webhookEntityWrapper.webhookClass())).thenReturn(Mono.error(new RuntimeException("Webhook call failed")));

        Mono<Transaction> transactionMono = webhookWebClientWrapper.sendWebhook(webhookEntityWrapper, "host", "path");

        // Then
        StepVerifier.create(transactionMono).expectNext(webhookEntityWrapper.object()).verifyComplete();

    }
}
