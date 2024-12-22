package com.complyt.v1.mappers.transaction;

import com.complyt.domain.transaction.Item;
import com.complyt.v1.mappers.JurisdictionalSalesTaxRuleMapper;
import com.complyt.v1.mappers.TimestampsMapper;
import com.complyt.v1.models.transaction.ItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = {TimestampsMapper.class})
public interface ItemMapper extends JurisdictionalSalesTaxRuleMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    Item itemDtoToItem(ItemDto itemDto);

    @Mapping(target = "jurisdictionalSalesTaxRules", expression = "java(combineJurisdictionalRules(item.getJurisdictionalSalesTaxRules(), item.getJurisdictionalTaxRules()))")
    ItemDto itemToItemDto(Item item);
}