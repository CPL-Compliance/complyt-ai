package com.complyt.services;

import com.complyt.business.data_fetcher.CountyFetcher;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.StatesMap;
import com.complyt.repositories.AddressWithSalesTaxRatesRepository;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AddressWithSalesTaxRatesServiceImpl implements AddressWithSalesTaxRatesService {

    @NonNull
    AddressWithSalesTaxRatesRepository addressWithSalesTaxRatesRepository;

    @NonNull
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @NonNull
    CountyFetcher countyFetcher;

    @Override
    public Mono<AddressWithSalesTaxRates> findByAddress(@NonNull Address address) {
        String collection = StatesMap.statesToCollections.get(address.getState());

        return addressWithSalesTaxRatesRepository.findByAddress(address, collection)
                .switchIfEmpty(salesTaxWebClientWrapper.findByAddress(address)
                        .flatMap(salesTaxData -> setBeforeSave(address, salesTaxData))
                        .flatMap(addressWithSalesTaxRates -> save(addressWithSalesTaxRates, collection)));
    }

    private Mono<AddressWithSalesTaxRates> setBeforeSave(@NonNull Address address, @NotNull SalesTaxData salesTaxData) {
        return countyFetcher.fetch(salesTaxData)
                .flatMap(county -> salesTaxDataToSalesTaxRate.map(salesTaxData)
                        .map(salesTaxRates -> {
                            Address addressWithCounty = address.withCounty(county);
                            return new AddressWithSalesTaxRates(null, addressWithCounty, salesTaxRates, LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));
                        }));
    }

    @Override
    public Mono<AddressWithSalesTaxRates> save(@NonNull AddressWithSalesTaxRates addressWithSalesTaxRates, @NonNull String collection) {
        return addressWithSalesTaxRatesRepository.save(addressWithSalesTaxRates, collection);
    }

}