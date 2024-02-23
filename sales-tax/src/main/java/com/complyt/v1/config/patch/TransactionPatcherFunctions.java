//package com.complyt.v1.config.patch;
//
//import com.complyt.utils.object_mapper.ComplytObjectMapper;
//import com.complyt.v1.models.TimestampsDto;
//import com.complyt.v1.models.transaction.*;
//
//import java.util.List;
//import java.util.UUID;
//import java.util.function.BiFunction;
//
//public interface TransactionPatcherFunctions {
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchDocumentName = (transcationDto, documentName) -> transcationDto.withDocumentName((String) documentName);
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchItems = (transcationDto, items) -> {
//        List<ItemDto> convertedItems = ComplytObjectMapper.convertToList(items, ItemDto.class);
//        return transcationDto.withItems(convertedItems);
//    };
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchBillingAddress = (transcationDto, billingAddress) -> {
//        OptionalAddressDto convertedBillingAddress = (OptionalAddressDto) ComplytObjectMapper.mapObject(billingAddress, OptionalAddressDto.class);
//        return transcationDto.withBillingAddress(convertedBillingAddress);
//    };
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchShippingAddress = (transcationDto, shippingAddress) -> {
//        MandatoryAddressDto convertedShippingAddress = (MandatoryAddressDto) ComplytObjectMapper.mapObject(shippingAddress, MandatoryAddressDto.class);
//        return transcationDto.withShippingAddress(convertedShippingAddress);
//    };
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchCustomerId = (transcationDto, customerId) -> {
//        UUID convertedCustomerId = (UUID) ComplytObjectMapper.mapObject(customerId, UUID.class);
//        return transcationDto.withCustomerId(convertedCustomerId);
//    };
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchExternalTimestamps = (transcationDto, externalTimestamps) -> {
//        TimestampsDto convertedExternalTimestamps = (TimestampsDto) ComplytObjectMapper.mapObject(externalTimestamps, TimestampsDto.class);
//        return transcationDto.withExternalTimestamps(convertedExternalTimestamps);
//    };
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchTransactionType = (transcationDto, transactionType) -> {
//        TransactionTypeDto e = TransactionTypeDto.valueOf((String) transactionType);
//        return transcationDto.withTransactionType(e);
//    };
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchShippingFee = (transcationDto, shippingFee) -> {
//        ShippingFeeDto convertedShippingFee = (ShippingFeeDto) ComplytObjectMapper.mapObject(shippingFee, TimestampsDto.class);
//        return transcationDto.withShippingFee(convertedShippingFee);
//    };
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchCreatedFrom = (transcationDto, createdFrom) -> transcationDto.withCreatedFrom((String) createdFrom);
//
//    BiFunction<TransactionDto, Object, TransactionDto> patchTransactionFilingStatus = (transcationDto, transactionFilingStatus) -> {
//        TransactionFilingStatusDto e = TransactionFilingStatusDto.valueOf((String) transactionFilingStatus);
//        return transcationDto.withTransactionFilingStatus(e);
//    };
//
//}