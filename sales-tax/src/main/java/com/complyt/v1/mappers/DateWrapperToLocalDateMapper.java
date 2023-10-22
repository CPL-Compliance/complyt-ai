package com.complyt.v1.mappers;

import com.complyt.v1.models.nexus.DateWrapperDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

@Mapper
public interface DateWrapperToLocalDateMapper {
    DateWrapperToLocalDateMapper INSTANCE = Mappers.getMapper(DateWrapperToLocalDateMapper.class);

    Logger log = LoggerFactory.getLogger(TimestampsMapper.class);

    default LocalDate dateWrapperToLocalDate(DateWrapperDto dateWrapperDto) {
        if (dateWrapperDto == null)
            return null;

        return LocalDate.parse(dateWrapperDto.date());
    }
}
