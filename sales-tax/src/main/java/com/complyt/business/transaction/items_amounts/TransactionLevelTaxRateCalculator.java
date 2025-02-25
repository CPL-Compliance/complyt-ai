//package com.complyt.business.transaction.items_amounts;
//
//import com.complyt.business.builder.CollectionBuilder;
//import com.complyt.domain.Taxable;
//import com.complyt.domain.transaction.Transaction;
//import lombok.AccessLevel;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import lombok.experimental.FieldDefaults;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//import java.util.Objects;
//
//@Component
//@AllArgsConstructor
//@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
//public class TransactionLevelTaxRateCalculator  implements AmountCalculator<List<Taxable>>  {
//
//    @NonNull
//    private CollectionBuilder<Taxable> taxableCollectionBuilder;
//
////    public Mono<Transaction> calculate(@NonNull Transaction transaction) {
////        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
////        BigDecimal totalItemsAmount = totalItemsAmountCalculator.calculate(taxables, transaction.getIsTaxInclusive());
////
////        return Objects.equals(transaction.getTotalItemsAmount(), BigDecimal.ZERO) ?
////                Mono.just(transaction) :
////                Mono.just(transaction.setSalesTax(transaction.getSalesTax()
////                        .withRate(transaction.getSalesTax().amount().divide(totalItemsAmount, 4, RoundingMode.HALF_UP))));
////    }
////
//
//    @Override
//    public BigDecimal calculate(@NonNull List<Taxable> taxables, Boolean isTaxInclusive) {
//        List<Taxable> taxables = (List<Taxable>) taxableCollectionBuilder.build(transaction);
//        BigDecimal totalItemsAmount = totalItemsAmountCalculator.calculate(taxables, transaction.getIsTaxInclusive());
//
//        return Objects.equals(transaction.getTotalItemsAmount(), BigDecimal.ZERO) ?
//                Mono.just(transaction) :
//                Mono.just(transaction.setSalesTax(transaction.getSalesTax()
//                        .withRate(transaction.getSalesTax().amount().divide(totalItemsAmount, 4, RoundingMode.HALF_UP))));
//    }
//}
