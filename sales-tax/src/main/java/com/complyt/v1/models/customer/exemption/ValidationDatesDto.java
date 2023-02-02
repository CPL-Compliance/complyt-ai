package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.timestamps.ComplytTimestampDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.*;

@EqualsAndHashCode
@ToString
@With
@Getter
@RequiredArgsConstructor
@Schema(name = "validationDates")
public class ValidationDatesDto {

    @Valid
    @NonNull
    private final ComplytTimestampDto fromDate;

    @Valid
    @NonNull
    private final ComplytTimestampDto toDate;
}
