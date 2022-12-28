package com.complyt.domain.timestamps;


import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
public class ComplytTimestamp {

    @NonNull
    private final LocalDateTime timestamp;
}
