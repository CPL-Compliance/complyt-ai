package com.complyt.services;

import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.StatesMap;
import com.complyt.repositories.SalesTaxRatesRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class SalesTaxRatesServiceImpl implements SalesTaxRatesService {

    @NonNull
    SalesTaxRatesRepository salesTaxRatesRepository;

    @NonNull
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @Override
    public Mono<SalesTaxRates> findByAddress(@NonNull Address address) {
        String collection = StatesMap.statesToCollections.get(address.getState());

        return salesTaxRatesRepository.findByAddress(address, collection)
                .switchIfEmpty(salesTaxWebClientWrapper.findByAddress(address)
                        .flatMap(salesTaxDataToSalesTaxRate::map)
                        .flatMap(salesTaxRates -> salesTaxRatesRepository.save(new AddressWithSalesTaxRates(address, salesTaxRates, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1)), collection)))
                .map(AddressWithSalesTaxRates::getSalesTaxRates);
    }

}