package io.complyt.domain.timestamps;

import io.complyt.annotations.Generated;
import lombok.*;

import java.time.LocalDateTime;

@With
@Generated
public record Timestamps(LocalDateTime createdDate, LocalDateTime updatedDate) {
}
