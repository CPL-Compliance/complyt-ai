package io.complyt.business.message_queue.rabbit_mq;

import io.complyt.business.webhook.web_clients.WebhookWebClientWrapper;
import io.complyt.domain.WebhookEntityWrapper;
import io.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import testUtils.unit_test.UnitTestUtilities;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ConsumerTest {

    @InjectMocks
    Consumer consumer;

    @Mock
    WebhookWebClientWrapper<Transaction> webhookWebClientWrapper;

    WebhookEntityWrapper webhookEntityWrapper;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        webhookEntityWrapper = testUtilities.createWebhookEntityWrapper();
    }

    @Test
    public void consume_consumes() {
        when(webhookWebClientWrapper.sendWebhook(webhookEntityWrapper)).thenReturn(Mono.just(webhookEntityWrapper));
        Mono<WebhookEntityWrapper<Transaction>> expectedWebhookEntityWrapperMono = consumer.consume(webhookEntityWrapper);
        StepVerifier.create(expectedWebhookEntityWrapperMono).expectNext(webhookEntityWrapper);
    }

}