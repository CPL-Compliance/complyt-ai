package com.complyt.domain.customer.exemption;

import com.complyt.domain.timestamps.ComplytTimestamp;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class ValidationDates {

    private final ComplytTimestamp fromDate;
    private final ComplytTimestamp toDate;

}
