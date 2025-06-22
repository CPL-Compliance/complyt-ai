package io.complyt.domain;

import io.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebhookEntityWrapperTest {

    WebhookEntityWrapper<Transaction> webhookEntityWrapper;

    UnitTestUtilities unitTestUtilities;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "");
        webhookEntityWrapper = unitTestUtilities.createWebhookEntityWrapper();
    }

    @Test
    void toString_SameStrings_Equal() {
        String referenceString = "WebhookEntityWrapper[id=" + webhookEntityWrapper.id() +
                ", timestamp=" + webhookEntityWrapper.timestamp() +
                ", action=" + webhookEntityWrapper.action() +
                ", webhookClass=" + webhookEntityWrapper.webhookClass() +
                ", object=" + webhookEntityWrapper.object() +
                ", host=" + webhookEntityWrapper.host() +
                ", path=" + webhookEntityWrapper.path() + "]";

        assertEquals(referenceString, webhookEntityWrapper.toString());
    }
}
