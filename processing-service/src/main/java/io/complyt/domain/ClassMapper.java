package io.complyt.domain;

import io.complyt.annotations.Generated;
import io.complyt.domain.nexus.SalesTaxTracking;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.transaction.Transaction;

import java.util.Map;

@Generated
public class ClassMapper {
    public static final Map<String, Class<? extends ComplytIdProperty>> CLASS_MAP = Map.of(
            "Transaction", Transaction.class,
            "SalesTaxTracking", SalesTaxTracking.class
    );
}
