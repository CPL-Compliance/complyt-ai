package testUtils.integration_test.templates.economic_nexus;

public interface EconomicNexusByAmountOrCountITTemplate extends EconomicNexusITTemplate {

    void upsertSalesTaxTracking_ResetNexusToNotEstablished_Returns200();

    void upsertTransaction_NewAndPassedNexusByCount_Returns201();

    void getSalesTaxTracking_CheckEconomicNexusEstablishedByCount_Returns200();
}
