package com.complyt.v1.models.transaction;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.MandatoryAddress;
import com.complyt.domain.transaction.MatchedAddressData;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Mapper(nullValueMappingStrategy = org.mapstruct.NullValueMappingStrategy.RETURN_NULL)
public interface AddressToMatchedAddressDataMapper {

    AddressToMatchedAddressDataMapper INSTANCE = Mappers.getMapper(AddressToMatchedAddressDataMapper.class);

    // Map Address to MandatoryAddressDto
    @Mapping(source = "city", target = "city")
    @Mapping(source = "country", target = "country")
    @Mapping(source = "county", target = "county")
    @Mapping(source = "state", target = "state")
    @Mapping(source = "street", target = "street")
    @Mapping(source = "region", target = "region")
    @Mapping(source = "zip", target = "zip")
    @Mapping(source = "isPartial", target = "isPartial")
    MandatoryAddress addressToMandatoryAddress(Address address);

    // Map Address to MatchedAddressData with scoring set to null by default
    @Mapping(target = "address", expression = "java(addressToMandatoryAddress(address))")
    @Mapping(target = "scoring", ignore = true) // Scoring will be null
    MatchedAddressData addressToMatchedAddressData(Address address);
}