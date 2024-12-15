package com.complyt.v1.mappers;

import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.v1.model.common_sales_tax_rates.CommonSalesTaxRatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CommonSalesTaxRatesMapper {

    CommonSalesTaxRatesMapper INSTANCE = Mappers.getMapper(CommonSalesTaxRatesMapper.class);

    CommonSalesTaxRatesDto commonSalesTaxRatesToCommonSalesTaxRatesDto(CommonSalesTaxRates commonSalesTaxRates);
}
