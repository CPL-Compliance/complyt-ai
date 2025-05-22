package com.complyt.business.transaction;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.security.TenantResolver;
import com.complyt.services.TransactionService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class RefundTransactionProcessorTest {

    @InjectMocks
    RefundTransactionProcessor refundTransactionProcessor;

    @Mock
    TransactionService transactionService;

    Transaction transaction;

    UnitTestUtilities testUtilities;

   

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
     public void isLinkedRefundFromAnInvoice_TransactionTypeNotRefund_returnFalse() {
        Transaction inputTransaction = transaction.setTransactionType(TransactionType.INVOICE);

        assertFalse(refundTransactionProcessor.isLinkedRefundFromAnInvoice(inputTransaction));
    }

    @Test
    public void isLinkedRefundFromAnInvoice_RefundLinkIsNull_returnFalse() {
        Transaction inputTransaction = transaction.setTransactionType(TransactionType.REFUND)
                .setIsRefundLinked(null);

        assertFalse(refundTransactionProcessor.isLinkedRefundFromAnInvoice(inputTransaction));
    }

    @Test
    public void isLinkedRefundFromAnInvoice_RefundLinkIsFalse_returnFalse() {
        Transaction inputTransaction = transaction.setTransactionType(TransactionType.REFUND)
                .setIsRefundLinked(false);

        assertFalse(refundTransactionProcessor.isLinkedRefundFromAnInvoice(inputTransaction));
    }

    @Test
    public void isLinkedRefundFromAnInvoice_CreatedFromIsNull_returnFalse() {
        Transaction inputTransaction = transaction.setTransactionType(TransactionType.REFUND)
                .setIsRefundLinked(true)
                .setCreatedFrom(null);

        assertFalse(refundTransactionProcessor.isLinkedRefundFromAnInvoice(inputTransaction));
    }

    @Test
    public void isLinkedRefundFromAnInvoice_AllChecksReturnTrue_returnTrue() {
        Transaction inputTransaction = transaction.setTransactionType(TransactionType.REFUND)
                .setIsRefundLinked(true)
                .setCreatedFrom("dummy");

        assertTrue(refundTransactionProcessor.isLinkedRefundFromAnInvoice(inputTransaction));
    }
}
