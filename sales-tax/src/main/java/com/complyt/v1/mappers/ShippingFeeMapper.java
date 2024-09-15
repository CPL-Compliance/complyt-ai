package com.complyt.v1.mappers;

import com.complyt.domain.transaction.ShippingFee;
import com.complyt.v1.models.transaction.ShippingFeeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = {TimestampsMapper.class})
public interface ShippingFeeMapper extends JurisdictionalSalesTaxRuleMapper {
    ShippingFeeMapper INSTANCE = Mappers.getMapper(ShippingFeeMapper.class);

    ShippingFeeDto shippingFeeDtoToShippingFee(ShippingFeeDto shippingFeeDto);

    @Mapping(target = "jurisdictionalSalesTaxRules", expression = "java(combineJurisdictionalRules(shippingFee.getJurisdictionalSalesTaxRules(), shippingFee.getJurisdictionalTaxRules()))")
    ShippingFeeDto shippingFeeToshippingFeeDto(ShippingFee shippingFee);
}