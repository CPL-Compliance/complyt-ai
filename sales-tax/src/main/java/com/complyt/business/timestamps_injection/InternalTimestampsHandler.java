package com.complyt.business.timestamps_injection;

import com.complyt.domain.properties.InternalTimestampsProperty;
import com.complyt.domain.timestamps.Timestamps;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class InternalTimestampsHandler<T extends InternalTimestampsProperty> {
    public T insertTimestampsToNew(@NonNull T newEntity) {
        LocalDateTime timestamps = LocalDateTime.now();
        Timestamps timeStamps = new Timestamps(timestamps, timestamps);

        return (T) newEntity.withInternalTimestamps(timeStamps);
    }

    public T insertTimestampsToExisting(@NonNull T newEntity, @NonNull T existingEntity) {
        LocalDateTime createdDate = existingEntity.getInternalTimestamps().getCreatedDate();
        LocalDateTime modifiedDate = LocalDateTime.now();
        Timestamps modifiedTimeStamps = new Timestamps(createdDate, modifiedDate);

        return (T) newEntity.withInternalTimestamps(modifiedTimeStamps);
    }
}
