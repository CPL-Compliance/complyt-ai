package com.complyt.v1.mappers;

import com.complyt.domain.*;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.v1.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper( OrderMapper.class );

    Order orderDtoToOrder(OrderDto orderDto);
    OrderDto orderToOrderDto(Order order);

    SalesTaxRateDto salesTaxRateToSalesTaxRateDto(SalesTaxRate salesTaxRate);
    SalesTaxRate salesTaxRateDtoToSalesTaxRate(SalesTaxRateDto salesTaxRateDto);

    OrderStatusDto orderStatusToOrderStatusDto(OrderStatus orderStatus);
    OrderStatus orderStatusDtoToOrderStatus(OrderStatusDto orderStatusDto);

    List<Item> itemDtosToItems(List<ItemDto> itemDtos);
    List<ItemDto> itemsToItemDtos(List<Item> items);

    Item itemDtoToItem(ItemDto itemDto);
    ItemDto itemToItemDto(Item item);

    TimeStamps timeStampsDtoToTimeStamps(TimeStampsDto timeStampsDto);
    TimeStampsDto timeStampsToTimeStampsDto(TimeStamps timeStamps);

    CustomerType customerTypeDtoToCustomerType(CustomerTypeDto customerTypeDto);
    CustomerTypeDto customerTypeToCustomerTypeDto(CustomerType customerType);

}
