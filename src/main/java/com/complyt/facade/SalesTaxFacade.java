package com.complyt.facade;

import com.complyt.service.SalesTaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SalesTaxFacade {
    @Autowired
    SalesTaxService salesTaxService;

    public String getSalesTax(String zip, String address, String city, String state) {
        return salesTaxService.getSalesTax(zip, address, city, state);
    }
}