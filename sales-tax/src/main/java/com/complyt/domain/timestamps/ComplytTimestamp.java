package com.complyt.domain.timestamps;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class ComplytTimestamp {
    private LocalDateTime timestamp;
}
