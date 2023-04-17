package integration.scenarios;

public interface EstimateAndSalesOrderITTemplate {

    void upsertTransaction_SalesOrderOverTheThreshold_Returns201();

    void upsertTransaction_EstimateOverTheThreshold_Returns201();

    void getSalesTaxTracking_CheckEconomicNexusNotPassed_Returns200();

    void upsertSalesTaxTracking_AddPhysicalNexus_Returns200();

    void upsertTransaction_EstimateAfterNexusApplied_Returns201WthTaxes();

    void upsertTransaction_SalesOrderAfterNexusApplied_Returns201WthTaxes();
}
