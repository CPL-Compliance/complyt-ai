package com.complyt.v1.config.patch;

import com.complyt.utils.object_mapper.ComplytObjectMapper;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.transaction.OptionalAddressDto;

import java.util.function.BiFunction;

public interface CustomerPatcher {

    BiFunction<CustomerDto, Object, CustomerDto> patchName = (customerDto, name) -> {
        return customerDto.withName((String) name);
    };

    BiFunction<CustomerDto, Object, CustomerDto> patchAddress = (customerDto, address) -> {
        OptionalAddressDto convertedAddress = (OptionalAddressDto) ComplytObjectMapper.mapObject(address, OptionalAddressDto.class);
        return customerDto.withAddress(convertedAddress);
    };

    BiFunction<CustomerDto, Object, CustomerDto> patchCustomerType = (customerDto, customerType) -> {
        CustomerTypeDto convertedCustomerTypeDto = (CustomerTypeDto) ComplytObjectMapper.mapObject(customerType, CustomerTypeDto.class);
        return customerDto.withCustomerType(convertedCustomerTypeDto);
    };

    BiFunction<CustomerDto, Object, CustomerDto> patchExternalTimestamps = (customerDto, externalTimestamps) -> {
        TimestampsDto convertedExternalTimestamps = (TimestampsDto) ComplytObjectMapper.mapObject(externalTimestamps, TimestampsDto.class);
        return customerDto.withExternalTimestamps(convertedExternalTimestamps);
    };

    BiFunction<CustomerDto, Object, CustomerDto> patchEmail = (customerDto, email) -> customerDto.withEmail((String) email);

}