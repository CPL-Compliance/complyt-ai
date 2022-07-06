package com.complyt.domain.nexus;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "nexus_tracking")
public class NexusTracking {
    private String state;
    private final ObjectId clientId;
    private PhysicalNexusTracker physicalNexusTracker;
    private EconomicNexusTracker economicNexusTracker;
}
