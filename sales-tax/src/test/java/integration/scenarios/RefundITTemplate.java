package integration.scenarios;

public interface RefundITTemplate {
    void upsertTransaction_NotPassingEconomicNexus_Returns201();

    void upsertTransaction_RefundBeforeEconomicNexusPassed_Returns201();

    void upsertTransaction_WouldHavePassedWithoutTheRefund_Returns201();

    void getSalesTaxTracking_checkEconomicNexusNotPassed_Returns200();

    void upsertSalesTaxTracking_AddPhysicalNexus_Returns200();

    void upsertTransaction_NewAfterPhysicalNexus_Returns201WithTaxes();

    void upsertTransaction_RefundOfHalfTheAmount_Returns201();
}
