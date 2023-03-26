package com.complyt.domain.customer.exemption;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class ValidationDates {

    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

}
