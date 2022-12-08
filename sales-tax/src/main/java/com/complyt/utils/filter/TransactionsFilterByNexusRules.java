package com.complyt.utils.filter;

import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.TransactionType;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TransactionsFilterByNexusRules implements ListFilter<Transaction, NexusStateRule> {

    @Override
    public List<Transaction> filter(List<Transaction> transactions, NexusStateRule nexusStateRule) {
        return transactions.stream()
                .filter(transaction -> isNexusCalculationRequiredForTransaction(nexusStateRule, transaction))
                .toList();
    }

    private boolean isNexusCalculationRequiredForTransaction(NexusStateRule nexusStateRule, Transaction transaction) {
        CustomerType customerType = transaction.getCustomer().getCustomerType();
        boolean customerTypeDoesNotExistInRule = !nexusStateRule.getCustomerTypes().contains(customerType);
        if (customerTypeDoesNotExistInRule) {
            log.debug("Customer of type " + customerType + " does not exist in state rule's customer types, transaction does not count in calculation");
            return false;
        }

        TransactionType transactionType = transaction.getTransactionType();
        boolean transactionTypeIsNotRequired = !List.of(TransactionType.INVOICE, TransactionType.REFUND).contains(transactionType);
        if (transactionTypeIsNotRequired) {
            log.debug("Transaction of type " + transactionType + " is not being included in nexus' calculation");
            return false;
        }

        TransactionStatus transactionStatus = transaction.getTransactionStatus();
        boolean transactionIsCancelled = transactionStatus.equals(TransactionStatus.CANCELLED);
        if (transactionIsCancelled) {
            log.debug("Transaction is cancelled therefore is not being included in nexus' calculation");
            return false;
        }

        return true;
    }
}
