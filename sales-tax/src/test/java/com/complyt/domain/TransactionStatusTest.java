package com.complyt.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionStatusTest {

    @Test
    public void TransactionStatus_GetActive_ReturnActive() {
        // Given + When
        TransactionStatus transactionStatus = TransactionStatus.ACTIVE;

        // Then
        assertEquals(TransactionStatus.valueOf("ACTIVE"), transactionStatus);
    }

    @Test
    public void TransactionStatus_GetCancelled_ReturnCancelled() {
        // Given + When
        TransactionStatus transactionStatus = TransactionStatus.CANCELLED;

        // Then
        assertEquals(TransactionStatus.valueOf("CANCELLED"), transactionStatus);
    }

    @Test
    public void TransactionStatus_GetPaid_ReturnPaid() {
        // Given + When
        TransactionStatus transactionStatus = TransactionStatus.PAID;

        // Then
        assertEquals(TransactionStatus.valueOf("PAID"), transactionStatus);
    }
}