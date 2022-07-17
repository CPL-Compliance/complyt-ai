package com.complyt.v1.model;

import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class NexusTrackingDto {
    private String id;
    private State state;
    private final ObjectId clientId;
    private boolean enforcesNexus;
    private PhysicalNexusTracker physicalNexusTracker;
    private EconomicNexusTracker economicNexusTracker;
}
