package testUtils.integration_test.templates.economic_nexus;

public interface EconomicNexusOnlyTaxableItemsITTemplate extends EconomicNexusITTemplate{

    void upsertTransaction_NewAndNotTaxableItem_Returns201();
}
