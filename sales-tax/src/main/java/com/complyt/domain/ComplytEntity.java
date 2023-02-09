package com.complyt.domain;

import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public abstract class ComplytEntity {
    protected final UUID complytId;
}
