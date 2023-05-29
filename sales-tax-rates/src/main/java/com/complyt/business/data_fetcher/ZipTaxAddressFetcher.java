package com.complyt.business.data_fetcher;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.mappers.address.ZipTaxDataToAddressMapper;
import com.complyt.domain.zip_tax.ZipTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class ZipTaxAddressFetcher implements AddressFetcher {

    @Override
    public Mono<Address> fetch(@NonNull SalesTaxData salesTaxData) {
        ZipTaxData zipTaxData = (ZipTaxData) salesTaxData;
        Address address = ZipTaxDataToAddressMapper.INSTANCE.map(zipTaxData);

        return Mono.just(address);
    }
}
