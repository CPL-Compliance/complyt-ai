package com.complyt.v1.models;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Schema(name = "JurisdictionalSalesTaxRules")
public class JurisdictionalSalesTaxRulesDto {

    private final String name;
    private final String abbreviation;
    private final boolean taxable;
    private final boolean specialTreatment;
    private final CalculationType calculationType;
    private final String description;
    private final float calculationValue;
    private final Map<String, CitySalesTaxRules> cities;
}
