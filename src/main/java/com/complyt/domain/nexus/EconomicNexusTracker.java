package com.complyt.domain.nexus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.With;

import java.util.Date;

@Getter
@AllArgsConstructor
@With
@ToString
public class EconomicNexusTracker{
    private boolean established;
    private Date establishedDate;
}
