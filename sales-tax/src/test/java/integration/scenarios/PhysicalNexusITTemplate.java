package integration.scenarios;

public interface PhysicalNexusITTemplate {
    void upsertTransaction_NewAndDoesntHavePhysicalNexus_Returns201NoTaxes();

    void upsertSalesTaxTracking_createdPhysicalNexus_Returns200();

    void upsertTransaction_NewAndAfterPhysicalNexus_Returns201WithTaxes();

    void upsertTransaction_NewAndBeforePhysicalNexus_Returns201NoTaxes();

    void upsertSalesTaxTracking_EnforcesSalesTaxToFalse_Returns200();

    void upsertTransaction_WithPhysicalNexusButNoEnforcedSalesTax_Returns201NoTaxes();
}
