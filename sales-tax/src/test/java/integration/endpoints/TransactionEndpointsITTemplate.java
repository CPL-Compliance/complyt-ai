package integration.endpoints;

import testUtils.integration_test.templates.endpoints.*;

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

    void upsertByExternalIdAndSource_OneItemIsNegativeAmount_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_TransactionWithShippingFee_ReturnsTaxableTransactionWithShippingFeeAndItemsCalculatedTotal();

    void upsertByExternalIdAndSource_ItemWithManualSalesTax_ReturnsTaxableTransactionWithCalculatedTotal();

    void upsertByExternalIdAndSource_NoItemHasDiscount_ReturnsTaxableTransactionWithDiscountTotal0();

    void upsertByExternalIdAndSource_OneItemHasDiscount_ReturnsTaxableTransactionWithDiscountTotal();

    void upsertByExternalIdAndSource_TwoItemHaveDiscount_ReturnsTaxableTransactionWithDiscountTotal();

    void upsertByExternalIdAndSource_OneItemHasDiscountOneItemIsNegative_ReturnsTaxableTransactionWithDiscount();

    void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNullAndTotalNotNull_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_ItemUnitPriceAndQuantityNotNullAndTotalNull_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_ItemDiscountIsEqualsToTotal_ReturnsTaxableTransactionWithItemAmount0();

    void upsertByExternalIdAndSource_ItemDiscountIsEqualsToUnitPriceMultiplyByQuantity_ReturnsTaxableTransactionWithItemAmount0();

    void upsertByExternalIdAndSource_ConflictingItemHasNoUnitPriceAndQuantityAndTotal_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingItemHasNegativeTotalAndDiscount_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingItemHasNegativeUnitPriceAndQuantityAndDiscount_Returns400ConflictedData();

    void upsertByExternalIdAndSource_ConflictingItemHasNegativeDiscount_Returns400ConflictedData();
}
