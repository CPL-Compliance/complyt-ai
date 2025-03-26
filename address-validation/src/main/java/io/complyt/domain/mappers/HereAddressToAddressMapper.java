package io.complyt.domain.mappers;

import io.complyt.business.address.CountryIsUsaChecker;
import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.Scoring;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.here.*;
import org.mapstruct.Mapper;
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
                .map(item -> {
                    HereAddress hereAddress = item.address();
                    boolean isUsa = CountryIsUsaChecker.isCountryUsa(hereAddress.countryName());

                    return new CachedAddressData(
                            mapAddress(item.address(), isUsa), // Map Address
                            mapScoring(item.scoring(), isUsa) // Map Scoring
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Maps the HereAddress to Address.
     *
     * @param hereAddress the HereAddress object to map
     * @param isUsa the address object, if true - swap region, state fields
     * @return the mapped Address object
     */
    default Address mapAddress(HereAddress hereAddress, boolean isUsa) {
        return new Address(
                hereAddress.city(), // City
                hereAddress.countryName(), // Country
                hereAddress.county(), // County
                isUsa ? hereAddress.state() : null, // State
                hereAddress.street(), // Street
                hereAddress.postalCode(), // Postal Code
                isUsa ? null : hereAddress.state(), // Region
                null // IsPartial
        );
    }

    /**
     * Maps the HereScoring to Scoring.
     *
     * @param scoring the HereScoring object to map
     * @param isUsa the address object, if true - swap region, state fields
     * @return the mapped Scoring object
     */
    default Scoring mapScoring(HereScoring scoring, boolean isUsa) {
        return new Scoring(
                MatchLevelType.fromScore(scoring.queryScore()), // Determine match level
                scoring.queryScore(),
                mapFieldsMatchScore(scoring.fieldScore(), isUsa) // Map field match scores
        );
    }

    /**
     * Maps the HereFieldScore to FieldsMatchScore.
     *
     * @param fieldScore the HereFieldScore object to map
     * @param isUsa the address object, if true - swap region, state fields
     * @return the mapped FieldsMatchScore object
     */
    default FieldsMatchScore mapFieldsMatchScore(HereFieldScore fieldScore, boolean isUsa) {
        FieldMatchType stateMatch = isUsa ? FieldMatchType.fromScore(fieldScore.state()) : null;
        FieldMatchType regionMatch = isUsa ? null : FieldMatchType.fromScore(fieldScore.state());

        return new FieldsMatchScore(
                FieldMatchType.fromScore(fieldScore.country()), // Map country match
                stateMatch,                                    // Map state match
                FieldMatchType.fromScore(fieldScore.city()),   // Map city match
                fieldScore.streets() == null ? null : FieldMatchType.fromScore(fieldScore.streets().get(0)), // Map street match
                FieldMatchType.fromScore(fieldScore.postalCode()), // Map postal code match
                regionMatch   // Map region match
        );
    }
}
