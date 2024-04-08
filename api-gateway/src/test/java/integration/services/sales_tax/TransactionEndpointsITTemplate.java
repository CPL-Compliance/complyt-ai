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

    void upsertByExternalIdAndSource_TransactionWithShippingFee_Returns200();

    void upsertByExternalIdAndSource_ItemWithManualSalesTax_Returns200();

    void upsertByExternalIdAndSource_NoItemHasDiscount_Returns200();

    void upsertByExternalIdAndSource_OneItemHasDiscount_Returns200();

    void upsertByExternalIdAndSource_TwoItemHaveDiscount_Returns200();

    void upsertByExternalIdAndSource_OneItemHasDiscountOneItemIsNegative_Returns200();

    void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNullAndTotalNotNull_Returns200();

    void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNotNullAndTotalNull_Returns200();

    void upsertByExternalIdAndSource_ItemDiscountIsEqualsToTotal_Returns200();

    void upsertByExternalIdAndSource_ItemDiscountIsEqualsToUnitPriceMultiplyByQuantity_Returns200();

    void upsertByExternalIdAndSource_ItemHasNoUnitPriceAndQuantityAndTotal_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingItemHasNegativeTotalAndDiscount_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingItemHasNegativeUnitPriceAndQuantityAndDiscount_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingItemHasNegativeDiscount_Returns400ConflictedData();

    void upsertByExternalIdAndSource_NonUsaCountry_Returns200WithTax();

    void upsertByExternalIdAndSource_NonUsaCountryWithRegion_Returns200WithTax();

    void upsertByExternalIdAndSource_NonUsaCountryWithRegionTaxInclusiveTrue_Returns200WithTaxFromTotal();

    void upsertByExternalIdAndSource_UsaCountryTaxInclusiveTrue_Returns200WithTaxFromTotal();

    void upsertByExternalIdAndSource_UsaCountryNoZipNoSupportedState_Returns400();

    void upsertByExternalIdAndSource_NotSupportedNonUsaCountryNo_Returns400();
}
