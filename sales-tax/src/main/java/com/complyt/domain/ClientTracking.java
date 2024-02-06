package com.complyt.domain;

import com.complyt.domain.timestamps.Timestamps;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
    private Nexus nexus;
    private String name;
    private Timestamps externalTimestamps;

}
