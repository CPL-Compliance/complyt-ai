package com.complyt.v1.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
@Schema(name = "JurisdictionalSalesTaxRules")
public class JurisdictionalSalesTaxRulesDto {
    private final String id;
    private final String name;
    private final String abbreviation;
    private final boolean taxable;
    private final boolean specialTreatment;
    private boolean isOverride;
    private final float value;
}
