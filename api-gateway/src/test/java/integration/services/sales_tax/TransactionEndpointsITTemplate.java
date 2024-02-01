package integration.services.sales_tax;

import integration.test_utils.templates.endpoints.*;

public interface TransactionEndpointsITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        UpsertByExternalIdAndSourceWithExemptionTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate,
        DeleteByExternalIdAndSourceITTemplate {

    void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404();

    void upsertByExternalIdAndSource_ExistsAndCustomerDoesntExists_Returns404();

    void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns500();

    void upsertByExternalIdAndSource_ExistsAndSaleTaxTrackingDoesntExists_Returns500();
    
    void upsertByExternalIdAndSource_ConflictingTransactionAmountIsNegative_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingTransactionItemTotalIsNotAligned_Returns400ConflictedData();
}
