package integration.scenarios;

public interface ShippingFeesITTemplate {

    void upsertTransaction_ShippingFeesNotPassingEconomicNexus_Returns200NoTaxes();

    void upsertTransaction_UpdateToHaveNoShippingFees_Returns200NoTaxes();

    void upsertTransaction_ShippingFeesNotPassingEconomicNexusAfterPreviousSubtraction_Returns200NoTaxes();

    void upsertTransaction_ShippingFeesNotTangibleAndNotAddedToThresholdCalculation_Returns200NoTaxes();

    void getSalesTaxTracking_checkEconomicNexusNotPassed_Returns200();

    void upsertTransaction_ShippingFeesPassingEconomicNexus_Returns200NoTaxes();

    void upsertSalesTaxTracking_ApproveEconomicNexus_Returns200();

    void upsertTransaction_ShippingFeesAfterNexusPassed_Returns200WithTaxes();

    void upsertTransaction_ShippingFeesNotTaxableAfterNexusPassed_Returns200NoTaxes();

    void upsertTransaction_ShippingFeesWithManualSalesTaxRate_Returns200WithManualTaxes();
}
