package com.complyt.domain.nexus;

public class NexusThreshold {
    private float amount;
    private int count;
    private Definition definition; // Specifying the way to check if threshold exceeded (e.g amount and count / amount or count)
}
