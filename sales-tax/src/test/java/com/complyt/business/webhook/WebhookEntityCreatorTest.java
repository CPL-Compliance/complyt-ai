package com.complyt.business.webhook;

import com.complyt.business.web_hook.WebhookEntityCreator;
import com.complyt.domain.WebhookEntityWrapper;
import com.complyt.domain.audit.Action;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

public class WebhookEntityCreatorTest {

    WebhookEntityCreator<Transaction> webhookEntityCreator;
    UnitTestUtilities testUtilities;
    Transaction transaction;

    @BeforeEach
    void setUp() {
        webhookEntityCreator = new WebhookEntityCreator<>();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    public void create_CreatesWebhookEntityWrapper_ReturnsWebhookEntityWrapper() {
        // Given + When
        Mono<WebhookEntityWrapper<Transaction>> webhookEntityWrapper = webhookEntityCreator.create(Transaction.class, transaction, Action.CREATE);

        StepVerifier.create(webhookEntityWrapper)
                .assertNext(result -> {
                    assert result != null;
                    assert result.webhookClass().equals(Transaction.class);
                    assert result.object().equals(transaction);
                })
                .verifyComplete();

    }
}
