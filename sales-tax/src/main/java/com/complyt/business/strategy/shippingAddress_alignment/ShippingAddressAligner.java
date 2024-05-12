package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.domain.transaction.Transaction;

public interface ShippingAddressAligner {
    Transaction align(Transaction transaction);
}
