package integration.scenarios;

public interface ShippingFeesITTemplate {

    void upsertTransaction_ShippingFeesNotPassingEconomicNexus_Returns200NoTaxes();

    void upsertTransaction_ShippingFeesNotTangibleAndNotAddedToThresholdCalculation_Returns200NoTaxes();

    void upsertTransaction_ShippingFeesPassingEconomicNexus_Returns200NoTaxes();

    void upsertSalesTaxTracking_ApproveEconomicNexus_Returns200();

    void upsertTransaction_ShippingFeesWithNexus_Returns200WithTaxes();

    void upsertTransaction_ShippingFeesWithNexusButNotTaxable_Returns200NoTaxes();
}
