package io.complyt.domain;

import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import lombok.With;

import java.util.List;

@With
public record ClientTracking(
        String id,
        String tenantId,
        Nexus nexus,
        String name,
        Timestamps internalTimestamps,
        List<String> subsidiaries,
        WebhookDetails webhookDetails
) implements InternalTimestampsProperty {
}
