package com.complyt.domain.customer.exemption;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ValidationDates {
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

}
