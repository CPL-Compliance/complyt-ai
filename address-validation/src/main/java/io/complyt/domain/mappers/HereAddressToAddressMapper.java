package io.complyt.domain.mappers;

import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.here.HereAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.domain.here.HereAddressItem;
import io.complyt.utils.observability.ContextLogger;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface HereAddressToAddressMapper {
    HereAddressToAddressMapper INSTANCE = Mappers.getMapper(HereAddressToAddressMapper.class);

    default CachedAddressData map(AddressData addressData) {
        HereAddressData hereAddressData = (HereAddressData) addressData;
        List<HereAddressItem> itemsList = hereAddressData.getItems();

        if (itemsList == null || itemsList.isEmpty() || itemsList.get(0) == null) {
            return CachedAddressData.DEFAULT;
        }

        return map(itemsList.get(0).address(), itemsList.get(0).scoring().queryScore());
    }

    @Mapping(source = "hereAddress.city", target = "city")
    @Mapping(source = "hereAddress.countryName", target = "country")
    @Mapping(source = "hereAddress.county", target = "county")
    @Mapping(source = "hereAddress.state", target = "state")
    @Mapping(source = "hereAddress.street", target = "street")
    @Mapping(source = "hereAddress.postalCode", target = "zip")
    @Mapping(source = "scoring", target = "score")
    CachedAddressData map(HereAddress hereAddress, double scoring);
}
