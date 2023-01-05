package com.complyt.domain.timestamps;


import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComplytTimestamp {

    @NonNull
    LocalDateTime timestamp;
}
