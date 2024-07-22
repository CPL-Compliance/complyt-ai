package testUtils.integration_test.templates.endpoints;

public interface RefreshITTemplate {
    void refreshByStateAndDate_Exists_Returns200WithSummaryAndNewNexusRule();

    void refreshByStateAndDate_ExistsAndHasNexus_Returns200NoSummary();

    void refreshByStateAndDate_DoesntExists_Returns404NotFound();

    void refreshByStateAndDate_DoesNotPassValidation_Returns400();

    void refreshByStateAndDate_DoesNotPassThreshold_returnsEconomicTrackerFalse();

    void refreshByStateAndDate_PassedNexus_returnsEconomicTrackerTrue();

    void refreshByStateNoRefDate_DoesNotPassThreshold_returnsEconomicTrackerFalse();

    void refreshByStateNoRefDate_PassedNexus_returnsEconomicTrackerTrue();
}
