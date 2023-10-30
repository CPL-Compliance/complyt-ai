package testUtils.integration_test.templates.economic_nexus;

public interface EconomicNexusOnlyTangibleItemsITTemplate extends EconomicNexusITTemplate {

    void upsertTransaction_NewAndNotTangibleItem_Returns201();

    void upsertTransaction_ChangedTangibilityToNotIncludedInNexusCalculation_Returns200();
}
