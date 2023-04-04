package testUtils.integration_test.templates.economic_nexus;

public interface EconomicNexusBySpecificCustomersITTemplate extends EconomicNexusITTemplate{

    void upsertTransaction_NewAndCustomerNotIncludedInNexusCalculation_Returns201();
}
