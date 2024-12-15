package com.complyt.domain.internal_rates;

import lombok.Value;
import lombok.With;

import java.time.LocalDateTime;

@With
@Value
public class InternalEffectiveDates {
    LocalDateTime state;
    LocalDateTime county;
    LocalDateTime city;
    LocalDateTime mta;
    LocalDateTime spd;
    LocalDateTime other1;
    LocalDateTime other2;
    LocalDateTime other3;
    LocalDateTime other4;
    LocalDateTime maxEffectiveDate;
}
