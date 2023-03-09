package com.complyt.v1.mappers;

import com.complyt.domain.customer.exemption.ValidationDates;
import com.complyt.v1.error_messages.DateErrorMessages;
import com.complyt.v1.models.customer.exemption.ValidationDatesDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL, uses = StringLocalDateTimeMapper.class)
public interface ValidationDatesMapper {
    ValidationDatesMapper INSTANCE = Mappers.getMapper(ValidationDatesMapper.class);


    @Mapping(target = "fromDate", source = "validationDates.fromDate", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "toDate", source = "validationDates.toDate", qualifiedByName = "localDateTimeToString")
    ValidationDatesDto validationDatesToValidationDatesDto(ValidationDates validationDates);

    @Mapping(target = "fromDate", source = "validationDatesDto.fromDate", qualifiedByName = "parseStringToLocalDateTime")
    @Mapping(target = "toDate", source = "validationDatesDto.toDate", qualifiedByName = "parseStringToLocalDateTime")
    ValidationDates validationDatesDtoToValidationDates(ValidationDatesDto validationDatesDto);


}
