package com.complyt.proxies;

import com.complyt.domain.matched_address.MatchedAddressData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "ADDRESS-VALIDATION")
public interface AddressValidationServiceProxy {

    @GetMapping("/v1/addresses/resolve")
    Mono<MatchedAddressData> validateAddress(
            @RequestParam(name = "state") String state, @RequestParam(name = "country") String country,
            @RequestParam(name = "county") String county, @RequestParam(name = "city") String city,
            @RequestParam(name = "street") String street, @RequestParam(name = "zip") String zip,
            @RequestParam(name = "isPartial") boolean isPartial
    );
}