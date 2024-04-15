package com.complyt.v1.mappers;

import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.v1.models.sales_tax.gt.ComplytGtRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ComplytGtRatesMapper {
    ComplytGtRatesMapper INSTANCE = Mappers.getMapper(ComplytGtRatesMapper.class);

    ComplytGtRates complytGtRatesDtoToComplytGtRates(ComplytGtRatesDto complytGstRatesDto);
    ComplytGtRatesDto complytGtRatesToComplytGtRatesDto(ComplytGtRates complytGstRates);

}
