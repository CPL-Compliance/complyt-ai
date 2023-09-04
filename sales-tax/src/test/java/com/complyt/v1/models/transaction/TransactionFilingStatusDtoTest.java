package com.complyt.v1.models.transaction;

import com.complyt.v1.models.transaction.TransactionFilingStatusDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionFilingStatusDtoTest {

    @Test
    public void TransactionFilingStatusDto_GetFiled_ReturnFiled() {
        // Given + When
        TransactionFilingStatusDto transactionFilingStatusDto = TransactionFilingStatusDto.FILED;

        // Then
        assertEquals(TransactionFilingStatusDto.valueOf("FILED"), transactionFilingStatusDto);
    }

    @Test
    public void TransactionFilingStatusDto_GetNotFiled_ReturnNotFiled() {
        // Given + When
        TransactionFilingStatusDto transactionFilingStatusDto = TransactionFilingStatusDto.NOT_FILED;

        // Then
        assertEquals(TransactionFilingStatusDto.valueOf("NOT_FILED"), transactionFilingStatusDto);
    }
}