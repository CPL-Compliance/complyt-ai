package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.ValidationDates;
import com.complyt.v1.models.customer.exemption.ValidationDatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = StringToLocalDateTimeMapper.class)
public interface ValidationDatesMapper {
    ValidationDatesMapper INSTANCE = Mappers.getMapper(ValidationDatesMapper.class);


    @Mapping(target = "fromDate", source = "fromDate", qualifiedByName = "parseLocalDateTimeToString")
    @Mapping(target = "toDate", source = "toDate", qualifiedByName = "parseLocalDateTimeToString")
    ValidationDatesDto validationDatesToValidationDatesDto(ValidationDates validationDates);

    @Mapping(target = "fromDate", source = "fromDate", qualifiedByName = "parseStringToLocalDateTime")
    @Mapping(target = "toDate", source = "toDate", qualifiedByName = "parseStringToLocalDateTime")
    ValidationDates validationDatesDtoToValidationDates(ValidationDatesDto validationDatesDto);


}
