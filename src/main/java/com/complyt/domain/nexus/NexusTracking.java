package com.complyt.domain.nexus;

import com.complyt.domain.State;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "nexus_tracking")
public class NexusTracking {
    @Id
    private String id;
    private State state;
    private final ObjectId clientId;
    private boolean enforcesNexus;
    private PhysicalNexusTracker physicalNexusTracker;
    private EconomicNexusTracker economicNexusTracker;
}
