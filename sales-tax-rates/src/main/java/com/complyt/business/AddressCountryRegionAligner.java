package com.complyt.business;

import com.complyt.domain.gt.GtAddress;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AddressCountryRegionAligner {
    public GtAddress align(@NonNull GtAddress gtAddress) {

        String country = TaxableCountryMap.countryMap.getOrDefault(gtAddress.country().toUpperCase(), gtAddress.country());
        String region = gtAddress.region() != null ? TaxableRegionsMap.taxableRegions.getOrDefault(gtAddress.region().toUpperCase(), null) : null;
        log.info("aligning received country: " + gtAddress.country() +  " and  region: " + gtAddress.region() + ", to be: country: " + country + " and region: " + region);

        return gtAddress.region() != null ? gtAddress.withRegion(region).withCountry(country) : gtAddress.withCountry(country);
    }
}