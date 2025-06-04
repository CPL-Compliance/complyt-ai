package io.complyt.domain.properties;


import io.complyt.domain.timestamps.Timestamps;

public interface InternalTimestampsProperty {

    Timestamps getInternalTimestamps();

    InternalTimestampsProperty withInternalTimestamps(Timestamps timestamps);
}
