package io.complyt.domain.nexus;

import io.complyt.domain.State;
import io.complyt.domain.customer.CustomerType;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.nexus.enums.TimeFrame;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@With
public record NexusStateRule(
        
        String id,
        boolean enforcesSalesTax,
        String country,
        State state,
        List<TaxableCategory> taxableCategories,
        List<TangibleCategory> tangibleCategories,
        List<CustomerType> customerTypes,
        TimeFrame timeFrame,
        NexusThreshold nexusThreshold,
        LocalDateTime appliedDate
) {
}
