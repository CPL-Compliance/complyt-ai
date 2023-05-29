package com.complyt.business.data_fetcher;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.mappers.address.FastTaxDataToAddressMapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class FastTaxAddressFetcher implements AddressFetcher {

    @Override
    public Mono<Address> fetch(@NonNull SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = (FastTaxData) salesTaxData;
        Address address = FastTaxDataToAddressMapper.INSTANCE.map(fastTaxData);
        return Mono.just(address);
    }

}
