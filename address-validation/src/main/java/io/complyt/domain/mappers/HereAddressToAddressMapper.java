package io.complyt.domain.mappers;

import io.complyt.business.address.UsaAbbreviations;
import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.Scoring;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.here.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface HereAddressToAddressMapper {
    HereAddressToAddressMapper INSTANCE = Mappers.getMapper(HereAddressToAddressMapper.class);

    default List<CachedAddressData> map(AddressData addressData) {
        HereAddressData hereAddressData = (HereAddressData) addressData;
        List<HereAddressItem> itemsList = hereAddressData.getItems();

        if (itemsList == null || itemsList.isEmpty() || itemsList.get(0) == null) {
            return List.of();
        }

        return itemsList.stream()
                .sorted((item1, item2) -> Double.compare(item2.scoring().queryScore(), item1.scoring().queryScore()))
                .map(item -> new CachedAddressData(
                        mapAddress(item.address()), // Map Address
                        mapScoring(item.scoring()) // Map Scoring
                ))
                .collect(Collectors.toList());    }

    /**
     * Maps the HereAddress to Address.
     *
     * @param hereAddress the HereAddress object to map
     * @return the mapped Address object
     */
    @Mapping(source = "hereAddress.city", target = "city")
    @Mapping(source = "hereAddress.countryName", target = "country")
    @Mapping(source = "hereAddress.county", target = "county")
    @Mapping(source = "hereAddress.state", target = "state")
    @Mapping(source = "hereAddress.street", target = "street")
    @Mapping(source = "hereAddress.postalCode", target = "zip")
    Address mapAddress(HereAddress hereAddress);

    /**
     * Maps the HereScoring to Scoring.
     *
     * @param scoring the HereScoring object to map
     * @return the mapped Scoring object
     */
    default Scoring mapScoring(HereScoring scoring) {
        return new Scoring(
                MatchLevelType.fromScore(scoring.queryScore()), // Determine match level
                scoring.queryScore(),
                mapFieldsMatchScore(scoring.fieldScore()) // Map field match scores
        );
    }

    /**
     * Maps the HereFieldScore to FieldsMatchScore.
     *
     * @param fieldScore the HereFieldScore object to map
     * @return the mapped FieldsMatchScore object
     */
    default FieldsMatchScore mapFieldsMatchScore(HereFieldScore fieldScore) {
        return new FieldsMatchScore(
                FieldMatchType.fromScore(fieldScore.country()), // Map country match
                FieldMatchType.fromScore(fieldScore.state()),   // Map state match
                FieldMatchType.fromScore(fieldScore.city()),    // Map city match
                fieldScore.streets() == null ? null : FieldMatchType.fromScore(fieldScore.streets().get(0)), // Map street match
                FieldMatchType.fromScore(fieldScore.postalCode()) // Map postal code match
        );
    }
}
