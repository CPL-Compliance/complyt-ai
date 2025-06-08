package com.complyt.business.webhook;

import com.complyt.business.web_hook.WebhookEntityCreator;
import com.complyt.business.web_hook.WebhookHandler;
import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.audit.Action;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class WebhookHandlerTest {

    @InjectMocks
    WebhookHandler<Transaction> webhookHandler;

    @Mock
    WebhookWebClientWrapper<Transaction> webhookWebClientWrapper;

    @Mock
    WebhookEntityCreator<Transaction> webhookEntityCreator;

    UnitTestUtilities testUtilities;

    Transaction transaction;

    ClientTracking clientTracking;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        clientTracking = testUtilities.createClientTracking("tenantId");
    }

    @Test
    public void handleWebhook_ShouldNotForwardRequestDueToNullWebhookDetails() {
        // Given + When
        Mono<Transaction> transactionMono = webhookHandler.handleWebhook(Transaction.class, transaction, clientTracking.getWebhookDetails(), Action.CREATE);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    public void handleWebhook_ShouldNotForwardRequestDueToFalseShouldForwardWriteOperations() {
        // Given
        ClientTracking clientTrackingToSend = clientTracking.withWebhookDetails(testUtilities.createWebhookDetails());

        // When
        Mono<Transaction> transactionMono = webhookHandler.handleWebhook(Transaction.class, transaction, clientTrackingToSend.getWebhookDetails(), Action.CREATE);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    public void handleWebhook_ShouldForwardRequest_SendsRequestToWebClientWrapper() {
        // Given
        ClientTracking clientTrackingToSend = clientTracking.withWebhookDetails(testUtilities.createWebhookDetails()
                .withShouldForwardWriteOperations(true)
                .withHost("host")
                .withPath("path"));
        WebhookEntityWrapper<Transaction> webhookEntityWrapper = testUtilities.createWebhookEntityWrapper();

        // When
        when(webhookEntityCreator.create(Transaction.class, transaction, Action.CREATE)).thenReturn(Mono.just(webhookEntityWrapper));
        when(webhookWebClientWrapper.sendWebhook(webhookEntityWrapper, clientTrackingToSend.getWebhookDetails().host(), clientTrackingToSend.getWebhookDetails().path())).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = webhookHandler.handleWebhook(Transaction.class, transaction, clientTrackingToSend.getWebhookDetails(), Action.CREATE);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }
}
