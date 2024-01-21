package com.complyt.services;

import com.complyt.business.data_fetcher.CityCountyFetcher;
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
    SalesTaxWebClientWrapper getBestMatchWebClientWrapper;

    /* in case of running with any profile other than fastTax, the same bean
       as getTaxInfoByCityCountyStateWebClientWrapper will be injected */
    @NonNull
    SalesTaxWebClientWrapper getTaxInfoByCityCountyStateWebClientWrapper;

    @NonNull
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @NonNull
    CityCountyFetcher cityCountyFetcher;

    @Override
    public Mono<ComplytSalesTaxRates> findByAddress(@NonNull Address address) {
        String collection = StatesMap.statesToCollections.get(address.state());

        return complytSalesTaxRatesRepository.findByAddress(address, collection)
                .switchIfEmpty(Mono.defer(() -> getBestMatchWebClientWrapper.findByAddress(address)
                        .flatMap(salesTaxData -> setBeforeSave(address, salesTaxData))
                        .flatMap(complytSalesTaxRates -> save(complytSalesTaxRates, collection))))
                .flatMap(this::modifyAddressIncasOfBlanks);
    }

    private Mono<ComplytSalesTaxRates> modifyAddressIncasOfBlanks(ComplytSalesTaxRates complytSalesTaxRates) {
        Address address = complytSalesTaxRates.address();
        return complytSalesTaxRates.address().city().isBlank() ?
                Mono.just(complytSalesTaxRates.withAddress(address.withCity(complytSalesTaxRates.requestAddress().city()))) :
                Mono.just(complytSalesTaxRates);
    }

    private Mono<ComplytSalesTaxRates> setBeforeSave(Address address, SalesTaxData salesTaxData) {
        return cityCountyFetcher.fetch(salesTaxData)
                .flatMap(cityCountyWrapper -> salesTaxDataToSalesTaxRate.map(salesTaxData)
                        .map(salesTaxRates -> {
                            Address modifiedAddress = address.withCity(cityCountyWrapper.city())
                                    .withCounty(cityCountyWrapper.county());

                            return new ComplytSalesTaxRates(null, modifiedAddress, address, salesTaxRates,
                                    LocalDateTime.now(), LocalDateTime.now().plusMonths(2));
                        }));
    }

    @Override
    public Mono<ComplytSalesTaxRates> save(@NonNull ComplytSalesTaxRates complytSalesTaxRates, @NonNull String collection) {
        return complytSalesTaxRatesRepository.save(complytSalesTaxRates, collection);
    }

}