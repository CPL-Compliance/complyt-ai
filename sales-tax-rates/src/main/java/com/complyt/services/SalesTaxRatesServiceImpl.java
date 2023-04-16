//package com.complyt.services;
//
//import com.complyt.domain.Address;
//import com.complyt.domain.AddressWithSalesTaxRates;
//import com.complyt.domain.SalesTaxRates;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//@AllArgsConstructor
//@Slf4j
//public class SalesTaxRatesServiceImpl implements SalesTaxRatesService {
//
//    @NonNull
//    AddressWithSalesTaxRatesServiceImpl addressWithSalesTaxRatesService;
//
//    public Mono<SalesTaxRates> findByAddress(@NonNull Address address) {
//        return addressWithSalesTaxRatesService.findByAddress(address)
//                .map(AddressWithSalesTaxRates::getSalesTaxRates);
//    }
//
//}