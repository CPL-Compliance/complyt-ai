package com.complyt.domain;

import com.complyt.domain.nexus.Subsidiary;
import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "client_tracking")
public class ClientTracking {
    @Id
    private final String id;
    private final String tenantId;
    private final Nexus nexus;
    private final String name;
    private final Timestamps internalTimestamps;
    private final List<Subsidiary> subsidiaries;
}