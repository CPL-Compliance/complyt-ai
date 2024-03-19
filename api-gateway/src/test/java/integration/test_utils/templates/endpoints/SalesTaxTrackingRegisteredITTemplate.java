package integration.test_utils.templates.endpoints;

public interface SalesTaxTrackingRegisteredITTemplate {
    void upsertByState_RegisteredAndDateNull_ReturnsSalesTaxTrackingWithDate();
    void upsertByState_RegisteredAndDate_ReturnsSalesTaxTrackingWithGivenDate();
    void upsertByState_NonRegisteredAndDateNull_ReturnsSalesTaxTracking();
    void upsertByState_NonRegisteredAndDate_Returns400();
}
