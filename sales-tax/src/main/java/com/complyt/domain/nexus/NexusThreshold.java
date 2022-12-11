package com.complyt.domain.nexus;

import com.complyt.domain.nexus.enums.Definition;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class NexusThreshold {
    private float amount;
    private int count;
    private Definition definition; // Specifying the way to check if threshold exceeded (e.g amount and count / amount or count)
}
