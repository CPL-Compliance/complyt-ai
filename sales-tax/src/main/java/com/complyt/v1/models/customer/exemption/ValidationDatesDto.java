package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.timestamps.ComplytTimestampDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@EqualsAndHashCode
@ToString
@With
@Getter
@RequiredArgsConstructor
@Schema(name = "validationDates")
public class ValidationDatesDto {

    @NonNull
    private final ComplytTimestampDto fromDate;
    @NonNull
    private final ComplytTimestampDto toDate;
}
