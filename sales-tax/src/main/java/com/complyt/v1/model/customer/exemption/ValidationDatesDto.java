package com.complyt.v1.model.customer.exemption;

import com.complyt.v1.model.timestamps.ComplytTimestampDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Schema(name = "Validation")
public class ValidationDatesDto {
    private final ComplytTimestampDto fromDate;
    private final ComplytTimestampDto toDate;
}
