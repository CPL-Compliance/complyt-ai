package com.complyt.v1.model;

import com.complyt.domain.sales_tax.product_classification.StateRules;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
@Document(collection = "ProductClassification")
public class ProductClassificationDto {
        private String id;
        private final String taxCode;
        private final String description;
        private final String title;
        private final List<StateRules> statesRules;
}
