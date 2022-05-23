package com.complyt.v1.model;

import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@With
@ToString
@Builder
@Schema(name = "ProductClassification")
public class ProductClassificationDto {
        private String id;
        private final String taxCode;
        private final String description;
        private final String title;
        private final List<JurisdictionalSalesTaxRules> statesRules;
}
