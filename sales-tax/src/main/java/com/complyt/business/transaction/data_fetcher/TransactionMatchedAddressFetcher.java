package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.v1.mappers.MatchedAddressMapper;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class TransactionMatchedAddressFetcher implements MatchedAddressFetcher {

    @NonNull
    AddressValidationWebClientWrapper<MatchedAddressDataDto> complytAddressValidationWebClientWrapper;

    @Override
    public Mono<MatchedAddressData> fetch(ShippingAddress address) {
        return complytAddressValidationWebClientWrapper.validateAddress(address)
                .map(MatchedAddressMapper.INSTANCE::matchedAddressDataDtoToMatchedAddress);
    }
}