package integration.scenarios;

public interface SalesTaxTrackingRegisteredITTemplate {
    void upsertByState_RegisteredAndDateNull_ReturnsSalesTaxTrackingWithDate();
    void upsertByState_RegisteredAndDate_ReturnsSalesTaxTrackingWithGivenDate();
    void upsertByState_NonRegisteredAndDateNull_ReturnsSalesTaxTracking();
    void upsertByState_NonRegisteredAndDate_Returns400();
}
