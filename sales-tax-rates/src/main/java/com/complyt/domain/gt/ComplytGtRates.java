package com.complyt.domain.gt;

import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@With
@Document(collection = "gt_rates")
public record ComplytGtRates(@Id String id, GtAddress gtAddress, GtRates gtRates) {
}
