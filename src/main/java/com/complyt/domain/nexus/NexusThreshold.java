package com.complyt.domain.nexus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

@Getter
@AllArgsConstructor
@With
@ToString
public class NexusThreshold {
    private float amount;
    private int count;
    private Definition definition; // Specifying the way to check if threshold exceeded (e.g amount and count / amount or count)
}
