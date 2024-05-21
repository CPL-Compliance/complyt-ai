package integration.scenarios;

public interface CancelledTransactionInSalesTaxTrackingITTemplate {

    void salesTaxTracking_upsertByCountryAndState_UsaCountryDoesntExists_Returns201();

    void transaction_upsertByExternalIdAndSource_ActiveUsaTransaction_InSalesTaxTrackingAndReturns201();

    void transaction_upsertByExternalIdAndSource_CancelledUsaTransaction_NotInSalesTaxTrackingAndReturns204();

    void transaction_deleteTransaction_ActiveUsaTransaction_NotInSalesTaxTrackingAndReturns204();

    void transaction_deleteTransaction_ActiveUsaTransactionPassesNexus_InSalesTaxTrackingAndReturns204();

}
