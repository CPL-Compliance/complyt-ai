package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.SalesTaxRate;

import java.util.List;

public interface SalesTaxRatesCalculator {

    default List<Taxable> provide(List<Taxable> taxables, SalesTaxRate salesTaxRate) {
        return null;
    }

    default Taxable provide(Taxable taxable, SalesTaxRate salesTaxRate) {
        return null;
    }

}
