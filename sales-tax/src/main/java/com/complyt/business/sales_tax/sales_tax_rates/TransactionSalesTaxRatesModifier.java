//package com.complyt.business.sales_tax.sales_tax_rates;
//
//import com.complyt.domain.Transaction;
//import com.complyt.domain.sales_tax.SalesTaxRate;
//import lombok.Getter;
//import lombok.NonNull;
//import lombok.Setter;
//
//@Getter
//@Setter
//public class TransactionSalesTaxRatesModifier {
//
//    @NonNull
//    private Transaction transaction;
//
//    @NonNull
//    private SalesTaxRate salesTaxRate;
//
//    private TransactionSalesTaxRatesModifier nextModifier;
//
//    public TransactionSalesTaxRatesModifier(@NonNull Transaction transaction, @NonNull SalesTaxRate salesTaxRate) {
//        this.transaction = transaction;
//        this.salesTaxRate = salesTaxRate;
//    }
//
//    public Transaction modify() {
//        if (nextModifier != null) {
//            nextModifier.modify();
//        }
//    }
//
//    public void add(TransactionSalesTaxRatesModifier next) {
//        if (nextModifier != null) {
//            nextModifier.add(next);
//        } else {
//            nextModifier = next;
//        }
//    }
//}
