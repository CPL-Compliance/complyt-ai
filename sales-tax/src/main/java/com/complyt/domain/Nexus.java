package com.complyt.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.time.LocalDateTime;

@AllArgsConstructor
@With
@Getter
@ToString
public class Nexus {
    private LocalDateTime taxableDate;
}
