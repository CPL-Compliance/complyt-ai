//package com.complyt.services;
//
//import com.complyt.domain.Address;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//@AllArgsConstructor
//@Slf4j
//public class CountyServiceImpl implements CountyService {
//
//    @NonNull
//    AddressWithSalesTaxRatesServiceImpl addressWithSalesTaxRatesService;
//
//    public Mono<String> findByAddress(@NonNull Address address) {
//        return addressWithSalesTaxRatesService.findByAddress(address)
//                .map(addressWithSalesTaxRates -> addressWithSalesTaxRates.getAddress().getCounty());
//    }
//
//}
