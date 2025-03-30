package io.complyt.authentication.domain.timestamps;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@With
public class Timestamps {
    @NonNull
    private final LocalDateTime createdDate;
    @NonNull
    private final LocalDateTime updatedDate;
}