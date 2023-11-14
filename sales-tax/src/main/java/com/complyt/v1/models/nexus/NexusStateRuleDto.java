package com.complyt.v1.models.nexus;

import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.TangibleCategoryDto;
import com.complyt.v1.models.TaxableCategoryDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import lombok.With;

import java.time.LocalDateTime;
import java.util.List;

@With
public record NexusStateRuleDto(
        boolean enforcesSalesTax,
        StateDto state,
        List<TaxableCategoryDto> taxableCategories,
        List<TangibleCategoryDto> tangibleCategories,
        List<CustomerTypeDto> customerTypes,
        TimeFrameDto timeFrame,
        NexusThresholdDto nexusThreshold,
        LocalDateTime appliedDate
) {
}