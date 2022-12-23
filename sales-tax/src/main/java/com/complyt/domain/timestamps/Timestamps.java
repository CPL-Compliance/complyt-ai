package com.complyt.domain.timestamps;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class Timestamps {
    private ComplytTimestamp createdDate;
    private ComplytTimestamp updatedDate;
}
