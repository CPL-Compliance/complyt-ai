package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    SalesTaxCalculator salesTaxCalculator;

    @NonNull
    @Autowired
    @Qualifier("fastTaxDataMapper")
    SalesTaxDataMapper salesTaxDataMapper;

    @Override
    public SalesTax getSalesTax(Address address, List<Item> items) {
        Mono<SalesTaxData> monoSalesTaxData = salesTaxWebClientWrapper
                .findByAddress(address.getZip(),address.getStreet(), address.getCity(), address.getState());

        SalesTaxData salesTaxData = monoSalesTaxData.block();
        SalesTaxRate salesTaxRate = SalesTaxDataMapper.INSTANCE.salesTaxDataToSalesTaxRate(salesTaxData);
        return salesTaxCalculator.calculate(salesTaxRate,items);

    }
}