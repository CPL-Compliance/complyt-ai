//package com.complyt.domain.mappers.address;
//
//import com.complyt.domain.Address;
//import com.complyt.domain.SalesTaxData;
//import com.complyt.domain.fast_tax.FastTaxData;
//import com.complyt.domain.fast_tax.TaxInfoItem;
//import com.complyt.domain.zip_tax.Result;
//import com.complyt.domain.zip_tax.ZipTaxData;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.factory.Mappers;
//
//@Mapper
//public interface ZipTaxDataToAddressMapper extends SalesTaxDataToAddressMapper {
//
//    ZipTaxDataToAddressMapper INSTANCE = Mappers.getMapper(ZipTaxDataToAddressMapper.class);
//
//    @Mapping(target = "city", source = "city")
//    @Mapping(target = "county", source = "county")
//    @Mapping(target = "state", source = "stateAbbreviation")
//    @Mapping(target = "zip", source = "zip")
//    Address map(Result result);
//
//    @Override
//    default Address map(SalesTaxData salesTaxData) {
//        ZipTaxData zipTaxData = ((ZipTaxData) salesTaxData);
//        Result result = zipTaxData.getResults().get(0);
//
//        return map(result);
//    }
//
//}