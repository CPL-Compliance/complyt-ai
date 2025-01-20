package integration.endpoints;

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
        DeleteByExternalIdAndSourceITTemplate,
        GetAllPaginationITTemplate {

    void upsertByExternalIdAndSource_NonUsaCountry_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_NonUsaCountryAndRegion_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_UsaCountryTaxInclusive_Returns200();

    void upsertByExternalIdAndSource_UsaCountryButSentAsAbbreviation_Returns201();

    void upsertByExternalIdAndSource_NonUsaCountryButSentAsAbbreviationReturnUpperCase_Returns201();

    void upsertByExternalIdAndSource_NonUsaCountryButSentLowerCaseReturnsUpperCase_Returns201();

    void upsertByExternalIdAndSource_NonUsaCountryAndRegionTaxInclusive_ReturnsTaxableTransaction();

    void upsertByExternalIdAndSource_NonUsaCountryNotSupported_Returns400();

    void upsertByExternalIdAndSource_UsaCountryWithNoState_Returns400();

    void upsertByExternalIdAndSource_UsaCountryWithNoZip_Returns400();

    void upsertByExternalIdAndSource_NonUsaCountryNotSupportedCountry_Returns400();

    void upsertByExternalIdAndSource_DoesntExistsAndCustomerDoesntExists_Returns404();

    void upsertByExternalIdAndSource_ExistsAndCustomerDoesntExists_Returns404();

    void upsertByExternalIdAndSource_DoesntExistsAndSaleTaxTrackingDoesntExists_Returns400();

    void upsertByExternalIdAndSource_ExistsAndSaleTaxTrackingDoesntExists_Returns400();

    void getByExternalIdAndSource_Exists_Returns200CheckingDefaultNullFields();

    void getByExternalIdAndSource_ExistsDetailedTrue_Returns200CheckingProjectedFields();

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

    void upsertByExternalIdAndSource_TransactionWithStatusCancelled_Returns204();

    void upsertByExternalIdAndSource_TransactionWithTransactionLevelDiscount_Returns201();

    void upsertByExternalIdAndSource_TransactionWithBothItemAndTransactionDiscount_Returns201();

    void upsertByExternalIdAndSource_UsaShippingWithSpacesInTheAddress_Returns200();

    void upsertByExternalIdAndSource_UsaTransactionWithNonExistingTaxCode_Returns400BadRequest();


    void upsertByExternalIdAndSource_UsaCountryTaxInclusiveTransactionTypeTaxableRefund_Returns200();

    void upsertByExternalIdAndSource_UsaCountryTransactionTypeTaxableRefundDidNotPassNexus_Returns200TransactionWithoutSalesTax();

    void upsertByExternalIdAndSource_UsaCountryTransactionTypeTaxableRefundDidNotPassNexus_Returns200TransactionAmountShouldBeSubtractedFromNexusSummaryAmount();

    void upsertByExternalIdAndSource_NewTransaction_PhysicalNexusTrackingTrue_salesTaxTrackingGotUpdated();

    void upsertByExternalIdAndSource_ExistingTransaction_PhysicalNexusTrackingTrue_salesTaxTrackingGotUpdated();

    // City County
    void upsertByExternalIdAndSource_UsaShippingAddressPassedNexus_Returns200AddressWithCityCounty();

    void upsertByExternalIdAndSource_UsaShippingAddressDidNotPassedNexus_Returns200AddressWithCityCounty();

    void upsertByExternalIdAndSource_UsaShippingAddressPassedNexus_Returns201AddressWithCityCounty();

    void upsertByExternalIdAndSource_UsaShippingAddressDidNotPassedNexus_Returns201AddressWithCityCounty();


    void upsertByExternalIdAndSource_UsaShippingAddressWithEurCurrency_ReturnsTransactionWithExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaShippingAddressWithEuroCurrency_ReturnsTransactionWithExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaShippingAddressWithUsdCurrency_ReturnsTransactionWithoutExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaShippingAddressWithUsDollarCurrency_ReturnsTransactionWithoutExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaShippingAddressWithNullCurrency_ReturnsTransactionWithoutExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaShippingAddressWithEuroCurrencyAndRefRate_ReturnsTransactionWithExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaShippingAddressWithNullCurrencyAndRefRate_ReturnsTransactionWithoutExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaShippingAddressWithEuroCurrencyAndFutureCreatedDate_ReturnsTransactionWithExchangeRateInfo();

    void upsertByExternalIdAndSource_UsaCountryWithPartialAddressWithoutState_Returns201();

    void upsertByExternalIdAndSource_UsaCountryWithPartialAddressAndBlankState_Returns201();

    void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxInclusive_Returns201();

    void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxExclusiveAndNewItems_Returns201();

    void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxInclusiveAndNewItems_Returns201();

    void upsertByExternalIdAndSource_UsaCountryTransactionWithTaxInclusiveAndNewItemsAndNoNexus_Returns201();

    void upsertByExternalIdAndSource_NonUsaCountryTransactionWithTaxInclusiveAndNewItems_Returns201();

    void getAll_PaginationSortedByCityAsc_ReturnsSortedTransactions();

    void getAll_PaginationSortedByCityDesc_ReturnsSortedTransactions();

    void getAll_PaginationFilteredByTransactionType_ReturnsInvoices();

    void getAll_PaginationFilteredByTransactionType_ReturnsRefunds();

    void getAll_PaginationFilteredByCityAndTransactionType_ReturnsTransactions();

    void getAll_DetailedTrue_Returns200CheckingProjectedFields();

    void upsert_TransactionIsLinkedRefund_Returns201WithFullSalesTaxOfInvoice();

    void upsert_TransactionIsLinkedRefundAndInvoiceNotFound_Returns201WithSameRefund();

    void upsert_TransactionIsLinkedRefundWithNullCreatedFrom_Returns201WithSameRefund();

    void upsert_IsLinkedRefundFieldIsNull_Returns201WithSameRefund();

    void upsert_TransactionIsLinkedRefundWithPercentage_Returns201WithHalfSalesTaxOfInvoice();

    void upsert_TransactionIsLinkedRefundWithPercentage_Returns201WithQuarterSalesTaxOfInvoice();

    void upsert_Transaction_RefundLinkedPercentageHasValueGreaterThan1_Returns400();

    void upsert_Transaction_RefundLinkedPercentageHasNegativeValue_Returns400();

    void upsert_TransactionIsLinkedButInvoiceHasSalesTaxNull_Returns201WithNullSalesTax();

    void getAll_PaginationFilteredByExternalId_fullIdSent_ReturnsTransaction();

    void getAll_PaginationFilteredByExternalId_PartialIdSent_ReturnsEmptyList();

    void upsertByExternalIdAndSource_NonUsaCountryTransactionWithNullZipAndTaxInclusiveAndNewItems_Returns201();

    // Testing taxableItemsAmount property
    void upsertByExternalIdAndSource_TransactionWithTaxableState_ReturnsTaxableItemsAmountOfItemsPrice();

    void upsertByExternalIdAndSource_TransactionWithTaxableCityAndNotTaxableState_ReturnsTaxableItemsAmountOfItemsPrice();

    void upsertByExternalIdAndSource_TransactionWithOutTaxableCityAndState_ReturnsTaxableItemsAmountOfZero();

    void upsertByExternalIdAndSource_TransactionWithOutTaxableCityAndStateWithZeroThatDoesNotExist_ReturnsTaxableItemsAmountOfZero();

    void upsertByExternalIdAndSource_GTTransactionWithTaxableState_ReturnsTaxableItemsAmountOfItemsPrice();

    void upsertByExternalIdAndSource_GTTransactionWithTaxableRegion_ReturnsTaxableItemsAmountOfItemsPrice();

    void upsertByExternalIdAndSource_GTTransactionWithOutTaxableRegionAndState_ReturnsTaxableItemsAmountOfZero();

    void upsertByExternalIdAndSource_TransactionWithOutTaxAndSalesTaxRules_ReturnsTaxableItemsAmountOfZero();

}