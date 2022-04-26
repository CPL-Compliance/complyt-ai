package com.complyt.v1.controllers;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.facades.SalesTaxFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Tag(name = "SalesTax", description = "This is the Sales Tax controller")
@AllArgsConstructor
@RestController
@RequestMapping(SalesTaxController.BASE_URL)
public class SalesTaxController {
    public static final String BASE_URL = "/v1/salesTax";

    private SalesTaxFacade salesTaxFacade;

    @Operation(summary = "Gets the sales tax of the given address")
    @GetMapping("")
    public Mono<SalesTaxData> getSalesTax(@RequestParam String zip,
                                          @RequestParam String address,
                                          @RequestParam String city,
                                          @RequestParam String state) {
        return salesTaxFacade.findByAddress(zip, address, city, state);
    }
}
