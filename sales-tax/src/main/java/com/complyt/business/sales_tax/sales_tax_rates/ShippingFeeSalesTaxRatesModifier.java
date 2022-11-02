//package com.complyt.business.sales_tax.sales_tax_rates;
//
//import com.complyt.domain.Transaction;
//import com.complyt.domain.sales_tax.SalesTaxRate;
//import lombok.NonNull;
//
//public class ShippingFeeSalesTaxRatesModifier extends TransactionSalesTaxRatesModifier {
//
//    @NonNull
//    private SalesTaxRatesCalculator salesTaxRatesCalculator;
//
//    public ShippingFeeSalesTaxRatesModifier(@NonNull Transaction transaction, @NonNull SalesTaxRate salesTaxRate, SalesTaxRatesCalculator salesTaxRatesCalculator) {
//        super(transaction, salesTaxRate);
//        this.salesTaxRatesCalculator = salesTaxRatesCalculator;
//    }
//}
