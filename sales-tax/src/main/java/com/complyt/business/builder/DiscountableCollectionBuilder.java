package com.complyt.business.builder;

import com.complyt.domain.Discountable;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class DiscountableCollectionBuilder implements CollectionBuilder<Discountable> {

    // currently not supporting shipping with discount
    @Override
    public Collection<Discountable> build(@NonNull Transaction transaction) {
        Collection<Discountable> discountables = new ArrayList<>(transaction.getItems());

        return discountables;
    }
}
