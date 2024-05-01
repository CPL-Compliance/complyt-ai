package com.complyt.v1.mappers;

import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.models.JurisdictionalSalesTaxRulesDto;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
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