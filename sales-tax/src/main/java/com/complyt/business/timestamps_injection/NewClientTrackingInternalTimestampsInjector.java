package com.complyt.business.timestamps_injection;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.timestamps.Timestamps;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class NewClientTrackingInternalTimestampsInjector implements TimestampsInjector<ClientTracking> {

    @NonNull
    private final ClientTracking clientTracking;

    @Override
    public ClientTracking inject() {
        LocalDateTime timestamp = LocalDateTime.now();
        Timestamps timeStamps = new Timestamps(timestamp, timestamp);

        return clientTracking.withInternalTimestamps(timeStamps);
    }
}
