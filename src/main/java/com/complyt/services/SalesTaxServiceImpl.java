package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@Service
@AllArgsConstructor
@Getter
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    @NonNull
    SalesTaxCalculator salesTaxCalculator;

    @Override
    public SalesTax getSalesTaxSync(Address address, List<Item> items) {
        SalesTaxData salesTaxData = salesTaxWebClientWrapper.findByAddressSync(address);
        SalesTaxRate salesTaxRate = salesTaxDataToSalesTaxRateMapper.map(salesTaxData);

        return salesTaxCalculator.calculate(salesTaxRate, items);
    }

}