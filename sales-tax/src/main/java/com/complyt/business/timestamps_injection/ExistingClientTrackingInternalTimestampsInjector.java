package com.complyt.business.timestamps_injection;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.ClientTrackingDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@AllArgsConstructor
public class ExistingClientTrackingInternalTimestampsInjector implements TimestampsInjector<ClientTracking> {

    @NonNull
    private final ClientTracking clientTracking;

    @Override
    public ClientTracking inject() {
        LocalDateTime createdDate = clientTracking.getInternalTimestamps().getCreatedDate();
        LocalDateTime modifiedDate = LocalDateTime.now();
        Timestamps modifiedTimeStamps = new Timestamps(createdDate, modifiedDate);

        return clientTracking.withInternalTimestamps(modifiedTimeStamps);
    }
}
