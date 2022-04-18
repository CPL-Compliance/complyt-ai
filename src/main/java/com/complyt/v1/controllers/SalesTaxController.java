package com.complyt.v1.controllers;

import com.complyt.domain.SalesTaxData;
import com.complyt.facades.SalesTaxFacade;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(SalesTaxController.BASE_URL)
public class SalesTaxController 
    public static final String BASE_URL = "/v1/salesTax";

    private SalesTaxFacade salesTaxFacade;

    @GetMapping("")
    public Mono<SalesTaxData> getSalesTax(@RequestParam String zip,
                                          @RequestParam String address,
                                          @RequestParam String city,
                                          @RequestParam String state) {
        return salesTaxFacade.findByAddress(zip, address, city, state);
    }
}
