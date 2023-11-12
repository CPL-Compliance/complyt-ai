package com.complyt.v1.models;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Map;

@Schema(name = "JurisdictionalSalesTaxRules")
public record JurisdictionalSalesTaxRulesDto(
        @Size(max = 256, message = "JurisdictionalSalesTaxRules.name " + StringErrorMessages.MAX_256_ERROR) String name,
        @Size(max = 256, message = "JurisdictionalSalesTaxRules.abbreviation " + StringErrorMessages.MAX_256_ERROR) String abbreviation,
        boolean taxable,
        boolean specialTreatment,
        CalculationType calculationType,
        @Size(max = 256, message = "JurisdictionalSalesTaxRules.description " + StringErrorMessages.MAX_256_ERROR) String description,
        BigDecimal calculationValue,
        Map<String, CitySalesTaxRules> cities) {

}
