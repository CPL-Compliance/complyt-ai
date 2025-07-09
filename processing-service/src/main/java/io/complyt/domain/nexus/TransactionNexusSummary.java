package io.complyt.domain.nexus;

import io.complyt.domain.transaction.TransactionType;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@With
public record TransactionNexusSummary(BigDecimal relevantAmount, LocalDateTime externalCreatedDate,
                                      TransactionType transactionType) {
}
