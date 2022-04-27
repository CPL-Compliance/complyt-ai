package com.complyt.v1.mappers;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.v1.model.OrderDto;
import com.complyt.v1.model.SalesTaxRateDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper( OrderMapper.class );

    Order orderDtoToOrder(OrderDto orderDto);
    OrderDto orderToOrderDto(Order order);

    SalesTaxRateDto orderToOrderDto(SalesTaxRate salesTaxRate);
}
