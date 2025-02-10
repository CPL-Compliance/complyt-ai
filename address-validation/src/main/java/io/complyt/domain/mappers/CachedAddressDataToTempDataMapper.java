package io.complyt.domain.mappers;

import io.complyt.annotations.Generated;
import io.complyt.domain.*;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Generated
@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
// Todo temp remove in phase 2
public interface CachedAddressDataToTempDataMapper {
    CachedAddressDataToTempDataMapper INSTANCE = Mappers.getMapper(CachedAddressDataToTempDataMapper.class);

    default TempAddressData map(CachedAddressData cachedAddressData) {
        Address address = cachedAddressData.address();
        Scoring scoring = cachedAddressData.scoring();

        return new TempAddressData(
                address.city(),
                address.country(),
                address.county(),
                address.state(),
                address.street(),
                address.zip(),
                false,  // Setting isPartial to false by default
                scoring.score()
        );
     }
}
