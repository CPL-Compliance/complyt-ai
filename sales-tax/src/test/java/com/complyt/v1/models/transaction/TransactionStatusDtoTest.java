package com.complyt.v1.models.transaction;

import com.complyt.v1.models.transaction.TransactionStatusDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionStatusDtoTest {

    @Test
    public void TransactionStatusDto_GetActive_ReturnActive() {
        // Given + When
        TransactionStatusDto transactionStatusDto = TransactionStatusDto.ACTIVE;

        // Then
        assertEquals(TransactionStatusDto.valueOf("ACTIVE"), transactionStatusDto);
    }

    @Test
    public void TransactionStatusDto_GetCancelled_ReturnCancelled() {
        // Given + When
        TransactionStatusDto transactionStatusDto = TransactionStatusDto.CANCELLED;

        // Then
        assertEquals(TransactionStatusDto.valueOf("CANCELLED"), transactionStatusDto);
    }

    @Test
    public void TransactionStatusDto_GetPaid_ReturnPaid() {
        // Given + When
        TransactionStatusDto transactionStatusDto = TransactionStatusDto.PAID;

        // Then
        assertEquals(TransactionStatusDto.valueOf("PAID"), transactionStatusDto);
    }
}