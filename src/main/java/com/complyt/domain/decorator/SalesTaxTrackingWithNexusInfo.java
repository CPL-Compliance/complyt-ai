package com.complyt.domain.decorator;

import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
public class SalesTaxTrackingWithNexusInfo {
    private final SalesTaxTracking salesTaxTracking;
    private final boolean hasNexus;
}
