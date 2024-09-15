package com.complyt.business.transaction;

import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.InvalidDiscountAmountException;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.complyt.v1.mappers.StringToLocalDateTimeMapper.log;

/**
 * this is used to calculate for each item the relative transaction discount that should apply on the item.
 * we use this class after the item discount already applied and new calculatedTotal was calculated.
 * there are two main stages here:
 * 1. recalculateItemsAfterTransactionLevelDiscount-
 *      1.1 Calculates the discount percentage of the total transaction amount
 *      1.2 Apply this discount to each item and updates the relativeTransactionDiscount accordingly
 * 2. applyRemainsOfDiscount-
 *      2.1 Calculate the remains that need to add\subtract from the given discount
 *      (the percentage can cause non-round numbers and their sum may not be equal to the discount amount)
 *      2.2 Apply the remains on the item with the highest amount
 */
@Component
public class TransactionDiscountCalculator implements DiscountCalculator {

    @Override
    public Mono<Transaction> injectRecalculatedTotalAfterDiscount(@NonNull Transaction transaction) {
        BigDecimal transactionTotalPrice = calculateTransactionTotalAmount(transaction.getItems());
        BigDecimal transactionDiscountPercentage = BigDecimalProcessor.removeTrailingZeros(
                transaction.getTransactionLevelDiscount().divide(transactionTotalPrice, 6, RoundingMode.HALF_UP));
        BigDecimal relativeDiscountPercentageForNewAmountCalculation = BigDecimal.ONE.subtract(transactionDiscountPercentage); // the percentage of price after the discount

        if (transactionDiscountPercentage.abs().compareTo(BigDecimal.ONE) > 0)
            return ContextLogger.observeCtx("<-- !!! Error occurred because transaction disocunt is higher than totalAmount ", log::error)
                    .then(Mono.error(InvalidDiscountAmountException::new));

        return Mono.just(transaction.setItems(
                        recalculateItemsAfterTransactionLevelDiscount(transaction.getItems(), relativeDiscountPercentageForNewAmountCalculation)
                ))
                .flatMap(this::applyRemainsOfDiscount);
    }

    private List<Item> recalculateItemsAfterTransactionLevelDiscount(List<Item> itemsList, BigDecimal percentage) {

        return itemsList.stream().map(item -> updateAmountAndDiscount(item, percentage))
                .collect(Collectors.toList());
    }

    private Item updateAmountAndDiscount(Item item, BigDecimal percentage) {
        BigDecimal itemAmountAfterTransactionDiscount = BigDecimalProcessor.removeTrailingZeros(
                calculateItemAmountAfterTransactionLevelDiscount(item, percentage));
        BigDecimal relativeTransactionDiscount = BigDecimalProcessor.removeTrailingZeros(
                item.getCalculatedTotal().subtract(itemAmountAfterTransactionDiscount));

        return item.setCalculatedTotal(itemAmountAfterTransactionDiscount).setRelativeTransactionDiscount(relativeTransactionDiscount);
    }

    private BigDecimal calculateItemAmountAfterTransactionLevelDiscount(Item item, BigDecimal percentage) {
        return item.getCalculatedTotal().multiply(percentage);

    }

    private BigDecimal calculateTransactionTotalAmount(@NonNull List<Item> items) {
        return items.stream().map(Item::getCalculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Mono<Transaction> applyRemainsOfDiscount(Transaction transaction) {
        List<Item> items = transaction.getItems();

        BigDecimal calculatedActualGivenTransactionDiscount = items.stream()
                .map(this::calculateGivenTransactionLevelDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainsTransactionDiscountToAdd = transaction.getTransactionLevelDiscount()
                .subtract(calculatedActualGivenTransactionDiscount);

        Optional<Item> highestAmountItem = items.stream()
                .max(Comparator.comparing(Item::getCalculatedTotal));

        // Flag to ensure we only apply the discount to one item
        AtomicBoolean updatedItemWithHighestAmount = new AtomicBoolean(false);

        List<Item> updatedItems = highestAmountItem.map(value ->
                items.stream().map(item ->
                        updateRemainsToHighestAmountItem(item, value, remainsTransactionDiscountToAdd, updatedItemWithHighestAmount)
                ).collect(Collectors.toList())).orElse(items);
        return Mono.just(transaction.setItems(updatedItems));
    }

    // For each item: calculatedTotal = totalPrice - discount - relativeTransactionDiscount
    private BigDecimal calculateGivenTransactionLevelDiscount(Item item) {
        BigDecimal itemDiscount = item.getDiscount() != null ? item.getDiscount() : BigDecimal.ZERO;

        return item.getTotalPrice().subtract(itemDiscount).subtract(item.getCalculatedTotal());
    }

    private Item updateRemainsToHighestAmountItem(Item item, Item highestAmountItem, BigDecimal remainsTransactionDiscountToAdd, AtomicBoolean updatedItemWithHighestAmount) {
        if (!updatedItemWithHighestAmount.get() && item.getCalculatedTotal().compareTo(highestAmountItem.getCalculatedTotal()) == 0) {
            updatedItemWithHighestAmount.set(true);
            return item
                    .setCalculatedTotal(BigDecimalProcessor.removeTrailingZeros(
                            item.getCalculatedTotal().subtract(remainsTransactionDiscountToAdd)))
                    .setRelativeTransactionDiscount(BigDecimalProcessor.removeTrailingZeros(
                            item.getRelativeTransactionDiscount().add(remainsTransactionDiscountToAdd)));
        }
        return item;
    }

}
