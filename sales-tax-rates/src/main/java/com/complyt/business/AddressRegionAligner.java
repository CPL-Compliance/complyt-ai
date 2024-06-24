package com.complyt.business;

import com.complyt.domain.gt.GtAddress;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddressRegionAligner {
    public GtAddress align(@NonNull GtAddress gtAddress) {
        if (gtAddress.region() == null) {
            return gtAddress;
        }

        String region = TaxableRegionsMap.taxableRegions.getOrDefault(gtAddress.region().toUpperCase(), null);
        log.info("aligning received region: " + gtAddress.region() + ", to be: " + region);

        return gtAddress.withRegion(region);
    }
}