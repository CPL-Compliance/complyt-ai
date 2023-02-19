package com.complyt.v1.models;


import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Generated;
import lombok.With;

import java.util.Map;

@Schema(name = "JurisdictionalSalesTaxRules")
public record JurisdictionalSalesTaxRulesDto(
        String name,
        String abbreviation,
        boolean taxable,
        boolean specialTreatment, CalculationType calculationType,
        String description,
        float calculationValue,
        Map<String, CitySalesTaxRules> cities) {

}
