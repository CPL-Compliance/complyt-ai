package testUtils.integration_test.templates.economic_nexus;

public interface EconomicNexusITTemplate {

    void upsertTransaction_NewAndDoesntPassedEconomicNexus_Returns201();

    void getSalesTaxTracking_CheckEconomicNexusNotPassed_Returns200();

    void upsertTransaction_NewAndPassedEconomicNexus_Returns201();

    void upsertSalesTaxTracking_ApproveEconomicNexus_Returns200();

    void upsertTransaction_NewInRangeOfEconomicNexus_Returns201WithSalesTax();

    void upsertTransaction_NewOutOfRangeOfEconomicNexus_Returns201();
}
