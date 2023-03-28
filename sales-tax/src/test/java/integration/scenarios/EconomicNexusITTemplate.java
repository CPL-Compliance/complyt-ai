package integration.scenarios;

public interface EconomicNexusITTemplate {

    void upsertTransaction_NewAndPassedEconomicNexus_Returns201();

    void upsertSalesTaxTracking_ApproveEconomicNexus_Returns200();

    void upsertTransaction_NewInRangeOfEconomicNexus_Returns201WithSalesTax();

    void upsertTransaction_NewOutOfRangeOfEconomicNexus_Returns201();
}
