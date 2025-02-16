package com.complyt.domain.mappers;

import com.complyt.domain.common_rates.CommonRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.internal_rates.InternalRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;


@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface InternalRatesToCommonRatesMapper {

    InternalRatesToCommonRatesMapper INSTANCE = Mappers.getMapper(InternalRatesToCommonRatesMapper.class);

    @Mapping(target = "complytId", source = "complytId")
    @Mapping(target = "salesTaxRates", source = "salesTaxRates", qualifiedByName = "mapSalesTaxRatesWithOtherSum")
    @Mapping(target = "address", source = "address")
    @Mapping(target = "source", expression = "java(SalesTaxSources.FAST_SALES_TAX)")
    @Mapping(target = "ratesMetaData", source = "internalSalesTaxRatesMetaData")
    CommonSalesTaxRates map(InternalSalesTaxRates internalSalesTaxRates);

    // Custom method to map InternalRates to CommonRates and calculate the sum of other1-4
    @Named("mapSalesTaxRatesWithOtherSum")
    default CommonRates mapSalesTaxRatesWithOtherSum(InternalRates internalRates) {
        if (internalRates == null) {
            return null;
        }

        BigDecimal otherSum = sumNonNull(
                internalRates.getOther1Rate(),
                internalRates.getOther2Rate(),
                internalRates.getOther3Rate(),
                internalRates.getOther4Rate()
        );

        return new CommonRates(internalRates.getStateRate(),
                internalRates.getCountyRate(),
                internalRates.getCityRate(),
                null,
                null,
                internalRates.getMtaRate(),
                internalRates.getSpdRate(),
                otherSum, // Set the sum of other1-4 as other
                internalRates.getTaxRate()
        );
    }

    // Helper method to calculate the sum of non-null BigDecimal values
    default BigDecimal sumNonNull(BigDecimal... values) {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal value : values) {
            if (value != null) {
                total = total.add(value);
            }
        }
        return total;
    }
}
