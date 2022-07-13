package com.complyt.domain.nexus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.time.LocalDate;
import java.util.Date;

@Getter
@AllArgsConstructor
@With
@ToString
public class PhysicalNexusTracker {
    private boolean established;
    private Date establishedDate;
}
