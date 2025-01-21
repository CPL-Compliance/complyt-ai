package com.complyt.business.transaction;

import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.services.TransactionService;
import com.complyt.utils.observability.ContextLogger;
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
        return ContextLogger.observeCtx("Searching for linked invoice by createdFrom: " + transaction.getCreatedFrom(), log::info)
                .then(transactionService.findByExternalIdAndSource(transaction.getCreatedFrom(), transaction.getSource()))
                .flatMap(invoice -> ContextLogger.observeCtx("Linked invoice found. Injecting the invoice's sales tax into the refund", log::info)
                        .thenReturn(processSalesTax(transaction, invoice)))
                .switchIfEmpty(Mono.defer(() -> ContextLogger.observeCtx("Linked invoice was NOT found. returning refund's original sales tax", log::info)
                        .then(Mono.just(transaction))));
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