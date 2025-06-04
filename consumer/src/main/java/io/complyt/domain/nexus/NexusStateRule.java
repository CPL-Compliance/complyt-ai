package io.complyt.domain.nexus;

import io.complyt.domain.State;
import io.complyt.domain.customer.CustomerType;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.nexus.enums.TimeFrame;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@With
@Document(collection = "nexus_state_rule")
public record NexusStateRule(
        @Id
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
