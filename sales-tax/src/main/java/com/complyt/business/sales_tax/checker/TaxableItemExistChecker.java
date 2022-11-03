package com.complyt.business.sales_tax.checker;

import com.complyt.domain.Item;
import com.complyt.domain.nexus.enums.TaxableCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TaxableItemExistChecker {

    public boolean hasTaxableItem(@NonNull List<Item> items) {
        for (Item item : items) {
            if (item.getTaxableCategory().equals(TaxableCategory.TAXABLE)) {
                log.debug("Taxable item found : " + item);
                return true;
            }
        }
        log.debug("No Taxable items found");
        return false;
    }
}
