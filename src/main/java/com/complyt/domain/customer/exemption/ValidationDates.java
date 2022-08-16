package com.complyt.domain.customer.exemption;

import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ValidationDates {
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

}
