package com.complyt.business.transaction;

import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.services.TransactionService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
@Slf4j
public class RefundTransactionProcessor {

    @NonNull
    private TransactionService transactionService;

    public boolean isLinkedRefundFromAnInvoice(Transaction transaction) {
        return transaction.getTransactionType() == TransactionType.REFUND &&
                transaction.getIsRefundLinked() != null &&
                transaction.getIsRefundLinked() &&
                transaction.getCreatedFrom() != null;
    }

    public Mono<Transaction> setInvoiceSalesTaxToLinkedRefund(Transaction transaction) {
        return transactionService.findByExternalIdAndSource(transaction.getCreatedFrom(), transaction.getSource())
                .map(invoice -> processSalesTax(transaction, invoice))
                .switchIfEmpty(Mono.just(transaction));
    }

    private static Transaction processSalesTax(Transaction refund, Transaction invoice) {
        SalesTax st = invoice.getSalesTax();
        if (st == null) {
            return refund.setSalesTax(null);
        }

        SalesTax salesTax = refund.getRefundLinkedPercentage() == null ? st :
                st.withAmount(st.amount().multiply(refund.getRefundLinkedPercentage()));

        return refund.setSalesTax(salesTax);
    }

}