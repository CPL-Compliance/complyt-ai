package com.complyt.domain.properties;

import com.complyt.domain.timestamps.Timestamps;

public interface InternalTimestampsProperty {

    Timestamps getInternalTimestamps();

    InternalTimestampsProperty withInternalTimestamps(Timestamps timestamps);
}
