package com.complyt.services;

import com.complyt.domain.SalesTaxData;

public interface SalesTaxService {
    SalesTaxData findByAddress(String zip, String address, String city, String state);
}
