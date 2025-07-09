package io.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebhookDetailsTest {

    WebhookDetails webhookDetails;

    UnitTestUtilities unitTestUtilities;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "");
        webhookDetails = unitTestUtilities.createWebhookDetails();
    }

    @Test
    void toString_SameStrings_Equal() {
        String referenceString = "WebhookDetails[shouldForwardWriteOperations=" + webhookDetails.shouldForwardWriteOperations() +
                ", host=" + webhookDetails.host() +
                ", path=" + webhookDetails.path() + "]";

        assertEquals(referenceString, webhookDetails.toString());
    }
}
