package com.complyt.services;

import com.complyt.business.collection_fetcher.UsaStatesMap;
import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.*;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.mappers.ComplytSalesTaxRatesToCommonRatesMapper;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@AllArgsConstructor
public class ExternalSalesTaxRatesServiceImpl <T extends ComplytSalesTaxRates> implements SalesTaxRatesService<ComplytSalesTaxRates> {

    @NonNull
    ComplytSalesTaxRatesRepository complytSalesTaxRatesRepository;

    @NonNull
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @NonNull
    ComplytIdHandler<ComplytSalesTaxRates> complytIdHandler;

    @Override
    public Mono<CommonSalesTaxRates> findByAddress(@NonNull AddressWithDate addressWithDate) {
        Address address = addressWithDate.getAddress();
        String collection = UsaStatesMap.statesToCollections.get(address.state().toUpperCase());

        return complytSalesTaxRatesRepository.findByAddress(address, collection)
                .map(ComplytSalesTaxRatesToCommonRatesMapper.INSTANCE::map)
                .switchIfEmpty(Mono.defer(() ->
                        salesTaxWebClientWrapper.findByAddress(address)
                                .flatMap(salesTaxData -> setBeforeSave(address, salesTaxData))
                                .flatMap(this::save)
                                .map(ComplytSalesTaxRatesToCommonRatesMapper.INSTANCE::map)));
    }

    private Mono<ComplytSalesTaxRates> setBeforeSave(Address address, SalesTaxData salesTaxData) {
        return salesTaxDataToSalesTaxRate.map(salesTaxData)
                        .map(salesTaxRates -> complytIdHandler.insertComplytIdToNew(
                                    new ComplytSalesTaxRates(null, null, address, salesTaxRates,
                                            LocalDateTime.now(), LocalDateTime.now().plusWeeks(2))));
    }


    @Override
    public Mono<ComplytSalesTaxRates> save(@NonNull ComplytSalesTaxRates complytSalesTaxRates) {
        String collection = UsaStatesMap.statesToCollections.get(complytSalesTaxRates.getAddress().state().toUpperCase());
        return complytSalesTaxRatesRepository.save(complytSalesTaxRates, collection);
    }
}