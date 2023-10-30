package testUtils.integration_test.templates.economic_nexus;

public interface EconomicNexusBySpecificCustomersITTemplate extends EconomicNexusITTemplate {

    void upsertTransaction_NewAndCustomerNotIncludedInNexusCalculation_Returns201();

    void upsertTransaction_ChangedCustomerToOneNotIncludedInNexusCalculation_Returns200();

    void upsertTransaction_SavedThenDeletedWithoutPassingNexus_Returns200();
}
