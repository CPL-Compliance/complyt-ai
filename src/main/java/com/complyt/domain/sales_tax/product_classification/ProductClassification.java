package com.complyt.domain.sales_tax.product_classification;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Document(collection = "product_classification")
public class ProductClassification {
    @Id
    private String id;
    private final String taxCode;
    private final String description;
    private final String title;
    private final Map<String,JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRules;
}
