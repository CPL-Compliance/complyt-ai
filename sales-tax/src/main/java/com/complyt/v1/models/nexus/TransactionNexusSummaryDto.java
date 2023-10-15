package com.complyt.v1.models.nexus;

import com.complyt.v1.models.transaction.TransactionTypeDto;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@With
public record TransactionNexusSummaryDto(BigDecimal relevantAmount, LocalDateTime externalCreatedDate,
                                         TransactionTypeDto transactionType) {
}
