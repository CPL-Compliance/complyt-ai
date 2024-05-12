package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.business.strategy.FunctionSelectorByTransactionAddressStrategy;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@AllArgsConstructor
@EqualsAndHashCode
public class ShippingAddressAlignmentStrategy extends FunctionSelectorByTransactionAddressStrategy {

    @NonNull
    ShippingAddressAligner usaAddressShippingAddressAligner;

    @NonNull
    ShippingAddressAligner nonUsaAddressShippingAddressAligner;
    
    @Override
    public Function<Transaction, Transaction> getFunctionForUsaOption(Transaction transaction) {
        return (givenTransaction) -> usaAddressShippingAddressAligner.align(givenTransaction);
    }

    @Override
    public Function<Transaction, Transaction> getFunctionForNonUsaOption(Transaction transaction) {
        return (givenTransaction) -> nonUsaAddressShippingAddressAligner.align(givenTransaction);
    }
}