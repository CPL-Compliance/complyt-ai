//package com.complyt.business.sales_tax.sales_tax_rates;
//
//import com.complyt.domain.Item;
//import com.complyt.domain.Transaction;
//import com.complyt.domain.sales_tax.SalesTaxRate;
//import lombok.Getter;
//import lombok.NonNull;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Getter
//public class ItemsSalesTaxRatesModifier extends TransactionSalesTaxRatesModifier {
//
//    @NonNull
//    private SalesTaxRatesCalculator salesTaxRatesCalculator;
//
//    public ItemsSalesTaxRatesModifier(@NonNull Transaction transaction, @NonNull SalesTaxRate salesTaxRate, SalesTaxRatesCalculator salesTaxRatesCalculator) {
//        super(transaction, salesTaxRate);
//        this.salesTaxRatesCalculator = salesTaxRatesCalculator;
//    }
//
//    @Override
//    public Transaction modify() {
//        List<Item> modifiedItems = super.getTransaction().getItems().stream()
//                .map(item -> item.withSalesTaxRate(salesTaxRatesCalculator.calculateSalesTaxRate(item.getJurisdictionalSalesTaxRules(), super.getSalesTaxRate())))
//                .collect(Collectors.toList());
//        super.setTransaction(super.getTransaction().withItems(modifiedItems));
//
//        super.modify();
//    }
//
//}
