package io.complyt.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import lombok.*;

import java.util.List;

@Data
@With
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientTracking implements InternalTimestampsProperty {
    private final String id;
    private final String tenantId;
    private final Nexus nexus;
    private final String name;
    private final Timestamps internalTimestamps;
    private final List<String> subsidiaries;
    private final WebhookDetails webhookDetails;

    @JsonCreator
    public ClientTracking(
            @JsonProperty("id") String id,
            @JsonProperty("tenantId") String tenantId,
            @JsonProperty("nexus") Nexus nexus,
            @JsonProperty("name") String name,
            @JsonProperty("internalTimestamps") Timestamps internalTimestamps,
            @JsonProperty("subsidiaries") List<String> subsidiaries,
            @JsonProperty("webhookDetails") WebhookDetails webhookDetails) {
        this.id = id;
        this.tenantId = tenantId;
        this.nexus = nexus;
        this.name = name;
        this.internalTimestamps = internalTimestamps;
        this.subsidiaries = subsidiaries;
        this.webhookDetails = webhookDetails;
    }


}