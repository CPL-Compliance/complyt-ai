package com.complyt.v1.mappers;

import com.complyt.domain.Customer;
import com.complyt.v1.model.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper( CustomerMapper.class );

    Customer customerDtoToCustomer(CustomerDto customerDto);
    CustomerDto customerToCustomerDto(Customer customer);

    List<CustomerDto> customersToCustomerDtos(List<Customer> customers);

    List<Customer> customerDtosToCustomers(List<CustomerDto> customerDtos);
}