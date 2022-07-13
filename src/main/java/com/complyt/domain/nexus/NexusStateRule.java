package com.complyt.domain.nexus;

import com.complyt.domain.CustomerType;
import com.complyt.domain.State;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "nexus_state_rule")
public class NexusStateRule {
    @Id
    private String id;
    private boolean enforcesNexus;
    private State state;
    private List<TaxableCategory> taxableCategories;
    private List<TangibleCategory> tangibleCategories;
    private List<CustomerType> customerTypes;
    private TimeFrame timeFrame;
    private NexusThreshold nexusThreshold;
}
