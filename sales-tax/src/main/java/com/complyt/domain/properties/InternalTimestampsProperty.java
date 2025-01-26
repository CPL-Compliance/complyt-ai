package com.complyt.domain.properties;

import com.complyt.domain.timestamps.Timestamps;

import java.sql.Time;

public interface InternalTimestampsProperty {

    Timestamps getInternalTimestamps();

    InternalTimestampsProperty withInternalTimestamps(Timestamps timestamps);
}
