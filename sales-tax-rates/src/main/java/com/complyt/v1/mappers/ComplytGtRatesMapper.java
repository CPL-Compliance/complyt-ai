package com.complyt.v1.mappers;

import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.v1.model.gt.ComplytGtRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ComplytGtRatesMapper {
    ComplytGtRatesMapper INSTANCE = Mappers.getMapper(ComplytGtRatesMapper.class);

    ComplytGtRates complytGtRatesDtoToComplytGtRates(ComplytGtRatesDto complytGtRatesDto);

    ComplytGtRatesDto complytGstRatesToComplytGstRatesDto(ComplytGtRates gtRates);
}
