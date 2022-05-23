package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
@Schema(name = "Rates")
public class RatesDto {
    private final float stateRate;
    private final float cityRate;
    private final float countyRate;
    private final float localRate;
}
