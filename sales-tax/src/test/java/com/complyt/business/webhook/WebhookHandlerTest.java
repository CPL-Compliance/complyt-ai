package com.complyt.business.webhook;

import com.complyt.business.web_hook.WebhookEntityCreator;
import com.complyt.business.web_hook.WebhookHandler;
import com.complyt.business.web_hook.web_clients.WebhookWebClientWrapper;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.transaction.Transaction;
import com.complyt.services.ClientTrackingService;
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
    ClientTrackingService clientTrackingService;

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
        // Given

        // When
        when(clientTrackingService.getClientTracking()).thenReturn(Mono.just(clientTracking));
        Mono<Transaction> transactionMono = webhookHandler.handleWebhook(Transaction.class,transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    public void handleWebhook_ShouldNotForwardRequestDueToFalseShouldForwardWriteOperations() {
        // Given
        ClientTracking clientTrackingToSend = clientTracking.withWebhookDetails(testUtilities.createWebhookDetails());
        // When
        when(clientTrackingService.getClientTracking()).thenReturn(Mono.just(clientTrackingToSend));
        Mono<Transaction> transactionMono = webhookHandler.handleWebhook(Transaction.class,transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }
}
