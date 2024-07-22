package integration.test_utils.templates.methods;

public interface RefreshITTemplate {
    void refresh_NonUsaEverythingExists_Returns200();

    void refresh_NoRefDate_PassedNexus_returnsEconomicTrackerTrue();

    void refresh_NoRefDate_DoesNotPassThreshold_returnsEconomicTrackerFalse();
}
