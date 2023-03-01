package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.timestamps.ComplytTimestampDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@With
@Schema(name = "validationDates")
public record ValidationDatesDto(
        @Valid @NotNull(message = "From Date may not be null") ComplytTimestampDto fromDate,
        @Valid @NotNull(message = "To Date may not be null") ComplytTimestampDto toDate) {

}
