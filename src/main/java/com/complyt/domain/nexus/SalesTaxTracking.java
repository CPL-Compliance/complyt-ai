package com.complyt.domain.nexus;

import com.complyt.domain.State;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "sales_tax_tracking")
public class SalesTaxTracking {
    @Id
    private String id;
    private State state;
    private final ObjectId clientId;
    private boolean enforcesSalesTax;
    private PhysicalNexusTracker physicalNexusTracker;
    private EconomicNexusTracker economicNexusTracker;
    private LocalDateTime appliedDate;
}
