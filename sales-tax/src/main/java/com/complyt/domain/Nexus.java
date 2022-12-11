package com.complyt.domain;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@With
@Getter
@ToString
@EqualsAndHashCode
public class Nexus {
    private LocalDateTime taxableDate;
}
