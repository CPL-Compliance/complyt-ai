package integration.scenarios;

public interface RefundITTemplate {
    void upsertTransaction_NotPassingEconomicNexus_Returns201();

    void upsertTransaction_RefundBeforeEconomicNexusPassed_Returns201();

    void upsertTransaction_WouldHavePassedWithoutTheRefund_Returns201();

    void getSalesTaxTracking_checkEconomicNexusNotPassed_Returns200();
}
