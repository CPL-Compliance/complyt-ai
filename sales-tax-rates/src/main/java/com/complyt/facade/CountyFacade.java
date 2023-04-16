//package com.complyt.facade;
//
//import com.complyt.domain.Address;
//import com.complyt.services.CountyService;
//import lombok.NonNull;
//import lombok.Value;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//@Component
//@Value
//public class CountyFacade {
//
//    @NonNull
//    CountyService countyServiceImpl;
//
//    public Mono<String> findByAddress(@NonNull Address address) {
//        return countyServiceImpl.findByAddress(address);
//    }
//
//}
