package com.complyt.business.timestamps_injection.provider;

import com.complyt.domain.nexus.SalesTaxTracking;

import java.time.LocalDateTime;

public interface AppliedDateProvider {
    LocalDateTime getAppliedDate(SalesTaxTracking salesTaxTracking, LocalDateTime updatedAppliedDate);
}
