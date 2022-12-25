package com.complyt.domain.timestamps;

import lombok.*;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
@With
public class Timestamps {

    @NonNull
    private final ComplytTimestamp createdDate;
    @NonNull
    private final ComplytTimestamp updatedDate;
}
