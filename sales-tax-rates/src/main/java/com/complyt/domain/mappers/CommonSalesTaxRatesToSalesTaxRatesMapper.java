package com.complyt.domain.mappers;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.SalesTaxRatesData;
import com.complyt.domain.matched_address.MatchedAddressData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.sql.Array;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CommonSalesTaxRatesToSalesTaxRatesMapper {

    CommonSalesTaxRatesToSalesTaxRatesMapper INSTANCE = Mappers.getMapper(CommonSalesTaxRatesToSalesTaxRatesMapper.class);

    @Mapping(target = "complytId", source = "commonSalesTaxRates.complytId")
    @Mapping(target = "requestAddress", source = "addressWithDate")
    @Mapping(target = "matchedAddressData", source = "matchedAddressData")
    @Mapping(target = "salesTaxRates", source = "commonSalesTaxRates.salesTaxRates")
    @Mapping(target = "source", source = "commonSalesTaxRates.source")
    SalesTaxRatesData map(AddressWithDate addressWithDate, MatchedAddressData matchedAddressData, CommonSalesTaxRates commonSalesTaxRates);

    default SalesTaxRatesData map(AddressWithDate addressWithDate, MatchedAddressData matchedAddressData, CommonSalesTaxRates commonSalesTaxRates, Boolean detailed) {
        SalesTaxRatesData result = map(addressWithDate, matchedAddressData, commonSalesTaxRates);
        if (!detailed) {
            return result.withRatesMetaData(null); // Remove the field when detailed is false
        }
        return result;
    }
}
