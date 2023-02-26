package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.timestamps.ComplytTimestampDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@With
@Schema(name = "validationDates")
public record ValidationDatesDto(
        @Valid @NotNull(message = "From Date timestamps may not be null") LocalDateTime fromDate,
        @Valid @NotNull(message = "To Date timestamps may not be null") LocalDateTime toDate) {

}
