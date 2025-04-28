package com.complyt.domain;

import com.complyt.domain.transaction.Transaction;
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
        boolean isPartial = false;
        String referenceString = "WebhookEntityWrapper[id=" + webhookEntityWrapper.id() +
                ", timestamp=" + webhookEntityWrapper.timestamp() +
                ", webhookClass=" + webhookEntityWrapper.webhookClass() +
                ", object=" + webhookEntityWrapper.object() + "]";

        assertEquals(referenceString, webhookEntityWrapper.toString());
    }
}
