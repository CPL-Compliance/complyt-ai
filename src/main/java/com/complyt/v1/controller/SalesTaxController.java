package com.complyt.v1.controller;

import com.complyt.facade.SalesTaxFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping(SalesTaxController.BASE_URL)
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SalesTaxController {
    public static final String BASE_URL = "/v1/salesTax";

    private SalesTaxFacade salesTaxFacade;

    @GetMapping("")
    public String getSalesTax(@RequestParam String zip, @RequestParam String address, @RequestParam String city,
                              @RequestParam String state) {
        return salesTaxFacade.getSalesTax(zip, address, city, state);
    }
}
