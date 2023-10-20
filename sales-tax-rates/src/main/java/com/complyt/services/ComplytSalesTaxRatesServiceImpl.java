package com.complyt.services;

import com.complyt.business.data_fetcher.CityCountyStateAddressFetcher;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.StatesMap;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ComplytSalesTaxRatesServiceImpl implements ComplytSalesTaxRatesService {

    @NonNull
    ComplytSalesTaxRatesRepository complytSalesTaxRatesRepository;

    @NonNull
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @NonNull
    CityCountyStateAddressFetcher cityCountyStateAddressFetcher;

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(@NonNull Address address) {
        String collection = StatesMap.statesToCollections.get(address.state());

        return complytSalesTaxRatesRepository.findByAddress(address, collection)
                .switchIfEmpty(Mono.defer(() -> salesTaxWebClientWrapper.findByAddress(address)
                        .flatMap(salesTaxData -> setBeforeSave(address, salesTaxData))
                        .flatMap(complytSalesTaxRates -> save(complytSalesTaxRates, collection))));
    }

    private Mono<ComplytSalesTaxRates> setBeforeSave(Address address, SalesTaxData salesTaxData) {
        return cityCountyStateAddressFetcher.fetch(salesTaxData)
                .flatMap(cityCountyState -> salesTaxDataToSalesTaxRate.map(salesTaxData)
                        .map(salesTaxRates -> {
                            Address modifiedAddress = address.withCity(cityCountyState.city()).withCounty(cityCountyState.county()).withState(cityCountyState.state());
                            return new ComplytSalesTaxRates(null, modifiedAddress, salesTaxRates, LocalDateTime.now(), LocalDateTime.now().plusMonths(2));
                        }));
    }

    @Override
    public Mono<ComplytSalesTaxRates> save(@NonNull ComplytSalesTaxRates complytSalesTaxRates, @NonNull String collection) {
        return complytSalesTaxRatesRepository.save(complytSalesTaxRates, collection);
    }

}