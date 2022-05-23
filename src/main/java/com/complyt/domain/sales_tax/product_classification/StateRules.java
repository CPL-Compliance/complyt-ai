package com.complyt.domain.sales_tax.product_classification;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
public class StateRules {
    @Id
    private final String id;
    private final String name;
    private final String abbreviation;
    private final boolean taxable;
    private final boolean specialTreatment;
    private final boolean isOverride;
    private final float value;
}
