package com.complyt.v1.mappers;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.v1.model.ItemDto;
import com.complyt.v1.model.TransactionDto;
import com.complyt.v1.model.TransactionStatusDto;
import com.complyt.v1.model.SalesTaxRateDto;
import com.complyt.domain.*;
import com.complyt.v1.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper( TransactionMapper.class );

    Transaction transactionDtoToTransaction(TransactionDto transactionDto);
    TransactionDto transactionToTransactionDto(Transaction transaction);

    SalesTaxRateDto salesTaxRateToSalesTaxRateDto(SalesTaxRate salesTaxRate);
    SalesTaxRate salesTaxRateDtoToSalesTaxRate(SalesTaxRateDto salesTaxRateDto);

    TransactionStatusDto transactionStatusToTransactionStatusDto(TransactionStatus transactionStatus);
    TransactionStatus transactionStatusDtoToTransactionStatus(TransactionStatusDto transactionStatusDto);

    List<Item> itemDtosToItems(List<ItemDto> itemDtos);
    List<ItemDto> itemsToItemDtos(List<Item> items);

    Item itemDtoToItem(ItemDto itemDto);
    ItemDto itemToItemDto(Item item);

    TimeStamps timeStampsDtoToTimeStamps(TimeStampsDto timeStampsDto);
    TimeStampsDto timeStampsToTimeStampsDto(TimeStamps timeStamps);

    CustomerType customerTypeDtoToCustomerType(CustomerTypeDto customerTypeDto);
    CustomerTypeDto customerTypeToCustomerTypeDto(CustomerType customerType);

}
