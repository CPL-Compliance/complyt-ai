package com.complyt.domain.nexus.checker;

import com.complyt.domain.nexus.NexusTracking;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NexusEnforcementCheck implements NexusCheck<NexusTracking> {

    @Override
    public boolean check(@NonNull NexusTracking nexusTracking) {
        return nexusTracking.isEnforcesNexus();
    }
}
