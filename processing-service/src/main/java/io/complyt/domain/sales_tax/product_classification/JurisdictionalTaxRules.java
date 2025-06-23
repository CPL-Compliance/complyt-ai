package io.complyt.domain.sales_tax.product_classification;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;

@With
@ToString
@Getter
@EqualsAndHashCode
public class JurisdictionalTaxRules implements TaxRules {
    private final String name;
    private final String abbreviation;
    private final boolean taxable;
    private final boolean specialTreatment;
    private final CalculationType calculationType;
    private final String description;
    private final BigDecimal calculationValue;
    private final Map<String, SubJurisdictionalTaxRules> regions;

    @JsonCreator
    public JurisdictionalTaxRules(
            @JsonProperty("name") String name,
            @JsonProperty("abbreviation") String abbreviation,
            @JsonProperty("taxable") boolean taxable,
            @JsonProperty("specialTreatment") boolean specialTreatment,
            @JsonProperty("calculationType") CalculationType calculationType,
            @JsonProperty("description") String description,
            @JsonProperty("calculationValue") BigDecimal calculationValue,
            @JsonProperty("regions") Map<String, SubJurisdictionalTaxRules> regions
    ) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.taxable = taxable;
        this.specialTreatment = specialTreatment;
        this.calculationType = calculationType;
        this.description = description;
        this.calculationValue = calculationValue;
        this.regions = regions;
    }

    public boolean calculatedByPercentageCheck() {
        return taxable && specialTreatment && calculationType == CalculationType.PERCENTAGE;
    }

    @Override
    public BigDecimal getCalculationValue() {
        return calculationValue != null ? calculationValue : BigDecimal.ZERO;
    }
}
