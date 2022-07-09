package com.complyt.domain.nexus;

import lombok.*;

import java.util.Date;

@Getter
@AllArgsConstructor
@With
@ToString
public class PhysicalNexusTracker {
    private boolean established;
    private Date establishedDate;
}
