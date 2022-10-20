package com.complyt.business.sales_tax;

import com.complyt.domain.Item;
import com.complyt.domain.ShippingFee;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class SalesTaxCalculationManager {

    @NonNull
    ItemsSalesTaxCalculator itemsSalesTaxCalculator;

    @NonNull
    ShippingFeeSalesTaxCalculator shippingFeeSalesTaxCalculator;

    public float calculate(@NonNull List<Item> items, ShippingFee shippingFee) {
        float itemsSalesTaxAmount = itemsSalesTaxCalculator.calculate(items);
        float shippingFeeSalesTaxAmount = shippingFee == null ? 0 : shippingFeeSalesTaxCalculator.calculate(shippingFee, items);
        float amount = itemsSalesTaxAmount + shippingFeeSalesTaxAmount;
        log.debug("Sales tax amount calculated : " + amount);

        return amount;
    }


}
