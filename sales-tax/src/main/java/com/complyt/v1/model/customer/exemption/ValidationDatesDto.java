package com.complyt.v1.model.customer.exemption;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class ValidationDatesDto {
    private final LocalDateTime fromDate;
    private final LocalDateTime toDate;

}
