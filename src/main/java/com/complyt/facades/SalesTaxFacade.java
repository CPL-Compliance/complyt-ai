package com.complyt.facades;

import com.complyt.domain.SalesTaxData;
import com.complyt.services.SalesTaxService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SalesTaxFacade {
    @Qualifier("zipTaxService")
    @NonNull
    private SalesTaxService salesTaxService;

    public SalesTaxData findByAddress(String zip, String address, String city, String state) {
        return salesTaxService.findByAddress(zip, address, city, state);
    }
}