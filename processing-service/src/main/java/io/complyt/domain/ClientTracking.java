package io.complyt.domain;

import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class ClientTracking implements InternalTimestampsProperty {
    private final String id;
    private final String tenantId;
    private final Nexus nexus;
    private final String name;
    private final Timestamps internalTimestamps;
    private final List<String> subsidiaries;
    private final WebhookDetails webhookDetails;

}