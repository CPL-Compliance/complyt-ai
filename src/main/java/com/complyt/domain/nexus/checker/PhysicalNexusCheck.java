package com.complyt.domain.nexus.checker;

import com.complyt.domain.nexus.NexusTracking;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PhysicalNexusCheck implements NexusCheck<NexusTracking> {
    @Override
    public boolean check(@NonNull NexusTracking nexusTracking) {
        return nexusTracking.getPhysicalNexusTracker().isEstablished();
    }
}
