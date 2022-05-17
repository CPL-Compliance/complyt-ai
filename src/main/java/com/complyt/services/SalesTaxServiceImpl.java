package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    private final SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    private final SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    @NonNull
    private final SalesTaxCalculator salesTaxCalculator;

    @Override
    public SalesTax getSalesTaxSync(Address address, List<Item> items) {
        SalesTaxData salesTaxData = salesTaxWebClientWrapper.findByAddressSync(address);
        SalesTaxRate salesTaxRate = salesTaxDataToSalesTaxRateMapper.map(salesTaxData);

        return salesTaxCalculator.calculate(salesTaxRate, items);
    }
}