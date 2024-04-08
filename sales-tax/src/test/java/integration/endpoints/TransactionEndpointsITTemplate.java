package integration.endpoints;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import testUtils.integration_test.templates.endpoints.*;

public interface TransactionEndpointsITTemplate extends
        UpsertByExternalIdAndSourceITTemplate,
        UpsertByExternalIdAndSourceWithExemptionTemplate,
        GetByExternalIdAndSourceITTemplate,
        GetByComplytIdITTemplate,
        GetAllITTemplate,
        GetAllBySourceTTemplate,
        DeleteByExternalIdAndSourceITTemplate {

    void upsertByExternalIdAndSource_NonUsaCountry_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_NonUsaCountryAndRegion_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_UsaCountryTaxInclusive_Returns200();

    void upsertByExternalIdAndSource_NonUsaCountryAndRegionTaxInclusive_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_NonUsaCountryNotSupported_Returns400();

    void upsertByExternalIdAndSource_UsaCountryWithNoState_Returns400();

    void upsertByExternalIdAndSource_UsaCountryWithNoZip_Returns400();

    void upsertByExternalIdAndSource_NonUsaCountryNotSupportedCountry_Returns400();

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
