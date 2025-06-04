package com.complyt.business.address_validation;

import com.complyt.annotations.Generated;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import com.complyt.v1.models.matched_address.ScoringDto;
import com.complyt.v1.models.matched_address.enums.FieldMatchType;
import com.complyt.v1.models.matched_address.enums.FieldsMatchScore;
import com.complyt.v1.models.matched_address.enums.MatchLevelType;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Mono;

@Generated
@EqualsAndHashCode
public class StubAddressValidationWebClientWrapper implements AddressValidationWebClientWrapper<MatchedAddressDataDto> {
    @Override
    public Mono<MatchedAddressDataDto> validateAddress(ShippingAddress address) {
        return validateAddress(address.city(), address.country(), address.county(),
                address.state(), address.region(), address.street(), address.zip(), address.isPartial());
    }

    @Override
    public Mono<MatchedAddressDataDto> validateAddress(String city, String country, String county, String state, String region, String street, String zip, Boolean isPartial) {
        MandatoryAddressDto address = new MandatoryAddressDto(
                city != null ? city : "",
                country != null ? country : "",
                county != null ? county : "",
                state != null ? state : "",
                street != null ? street : "",
                region != null ? region : "",
                zip != null ? zip : "",
                isPartial);
        ScoringDto scoringDto = new ScoringDto(MatchLevelType.EXCELLENT, 0.9, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));
        MatchedAddressDataDto matchedAddressDataDto = new MatchedAddressDataDto(address, scoringDto);
        return Mono.just(matchedAddressDataDto);
    }
}