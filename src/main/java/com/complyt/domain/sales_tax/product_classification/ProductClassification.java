package com.complyt.domain.sales_tax.product_classification;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
@Document(collection = "Product_Classification")
public class ProductClassification {
    @Id
    private String id;
    private final String taxCode;
    private final String description;
    private final String title;
    private final List<StateRules> statesRules;
}
