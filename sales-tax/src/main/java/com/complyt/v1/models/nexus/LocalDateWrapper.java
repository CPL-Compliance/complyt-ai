package com.complyt.v1.models.nexus;

import lombok.With;

import java.time.LocalDate;

@With
public record LocalDateWrapper(
    LocalDate date
) {
}

