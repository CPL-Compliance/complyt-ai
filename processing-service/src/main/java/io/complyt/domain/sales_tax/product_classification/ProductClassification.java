package io.complyt.domain.sales_tax.product_classification;

import io.complyt.domain.nexus.enums.TangibleCategory;
import lombok.*;

import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
public class ProductClassification {
    
    private String id;
    private final String taxCode;
    private final String description;
    private final String title;
    private final Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRules;
    private final Map<String, JurisdictionalTaxRules> jurisdictionalTaxRules;
    private TangibleCategory tangibleCategory;
}
