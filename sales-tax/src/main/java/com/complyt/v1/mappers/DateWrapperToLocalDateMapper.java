package com.complyt.v1.mappers;

import com.complyt.v1.models.nexus.DateWrapperDto;
import com.complyt.v1.models.nexus.LocalDateWrapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Optional;

@Mapper
public interface DateWrapperToLocalDateMapper {
    DateWrapperToLocalDateMapper INSTANCE = Mappers.getMapper(DateWrapperToLocalDateMapper.class);

    default LocalDateWrapper dateWrapperToLocalDateWrapper(DateWrapperDto dateWrapperDto) {
        if (dateWrapperDto == null || dateWrapperDto.date() == null) {
            return new LocalDateWrapper(null);
        }

        return new LocalDateWrapper(LocalDate.parse(dateWrapperDto.date()));
    }
}
