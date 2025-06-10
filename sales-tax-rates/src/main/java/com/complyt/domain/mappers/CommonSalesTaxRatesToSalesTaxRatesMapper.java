package com.complyt.domain.mappers;

import com.complyt.domain.AddressWithDate;
import com.complyt.domain.common_rates.CommonAddress;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.SalesTaxRatesData;
import com.complyt.domain.internal_rates.FilingMetaData;
import com.complyt.domain.internal_rates.InternalSalesTaxRatesMetaData;
import com.complyt.domain.matched_address.MatchedAddressData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CommonSalesTaxRatesToSalesTaxRatesMapper {

    CommonSalesTaxRatesToSalesTaxRatesMapper INSTANCE = Mappers.getMapper(CommonSalesTaxRatesToSalesTaxRatesMapper.class);

    @Mapping(target = "complytId", source = "commonSalesTaxRates.complytId")
    @Mapping(target = "requestAddress", source = "addressWithDate")
    @Mapping(target = "matchedAddressData", source = "matchedAddressData")
    @Mapping(target = "salesTaxRates", source = "commonSalesTaxRates.salesTaxRates")
    @Mapping(target = "source", source = "commonSalesTaxRates.source")
    @Mapping(target = "filingMetaData", expression = "java(toFilingMetaData(commonSalesTaxRates))")
    SalesTaxRatesData map(AddressWithDate addressWithDate, MatchedAddressData matchedAddressData, CommonSalesTaxRates commonSalesTaxRates);

    default SalesTaxRatesData map(AddressWithDate addressWithDate, MatchedAddressData matchedAddressData, CommonSalesTaxRates commonSalesTaxRates, Boolean detailed) {
        SalesTaxRatesData result = map(addressWithDate, matchedAddressData, commonSalesTaxRates);
        if (!detailed) {
            return result.withFilingMetaData(null); // Remove the field when detailed is false
        }
        return result;
    }

    default FilingMetaData toFilingMetaData(CommonSalesTaxRates commonSalesTaxRates) {
        if (commonSalesTaxRates == null || commonSalesTaxRates.ratesMetaData() == null || commonSalesTaxRates.address() == null) {
            return null;
        }

        CommonAddress address = commonSalesTaxRates.address();
        InternalSalesTaxRatesMetaData metaData = commonSalesTaxRates.ratesMetaData();

        return new FilingMetaData(
                address.city(),
                address.county(),
                metaData.getOther1Rate(),
                metaData.getOther2Rate(),
                metaData.getOther3Rate(),
                metaData.getOther4Rate(),
                metaData.getCountyRptCode(),
                metaData.getCityRptCode(),
                metaData.getMtaName(),
                metaData.getMtaNumber(),
                metaData.getSpdName(),
                metaData.getSpdNumber(),
                metaData.getOther1Name(),
                metaData.getOther1Number(),
                metaData.getOther2Name(),
                metaData.getOther2Number(),
                metaData.getOther3Name(),
                metaData.getOther3Number(),
                metaData.getOther4Name(),
                metaData.getOther4Number(),
                metaData.getFipsCounty()
        );
    }
}
