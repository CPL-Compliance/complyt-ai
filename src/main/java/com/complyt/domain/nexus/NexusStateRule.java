package com.complyt.domain.nexus;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "nexus_state_rule")
public class NexusStateRule {
    private boolean enforcesNexus;
    private String state;
    private List<TaxableCategory> taxableCategories;
    private List<TangibleCategory> tangibleCategories;
    private List<CustomerType> customerTypes;
    private TimeFrame timeFrame;
    private NexusThreshold threshold;
}
