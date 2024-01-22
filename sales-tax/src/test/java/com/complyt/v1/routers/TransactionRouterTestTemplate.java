package com.complyt.v1.routers;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import testUtils.unit_test.templates.endpoints.*;
import testUtils.unit_test.templates.validations.ExternalTimestampsValidationTestTemplate;
import testUtils.unit_test.templates.validations.InternalTimestampsValidationTestTemplate;
import testUtils.unit_test.templates.validations.ItemValidationTestTemplate;
import testUtils.unit_test.templates.validations.ShippingFeeValidationTestTemplate;

public interface TransactionRouterTestTemplate extends
        GetByExternalIdAndSourceRouterTestTemplate,
        GetByComplytIdRouterTestTemplate,
        GetAllRouterTestTemplate,
        GetAllBySourceRouterTestTemplate,
        DeleteByExternalIdAndSourceRouterTestTemplate,
        // Validation::ExternalId, Source, ComplytId
        UpsertByExternalIdAndSourceRouterTestTemplate,
        // Validation::InternalTimestamps
        InternalTimestampsValidationTestTemplate,
        // Validation::ExternalTimestamps
        ExternalTimestampsValidationTestTemplate,
        // Validation::Item in Items
        ItemValidationTestTemplate,
        // Validation::ShippingFee
        ShippingFeeValidationTestTemplate {

    void getByExternalIdAndSource_ExistsWithSalesTax_Returns200();

    void getAny_InvalidUrl_Returns404();

    void putAny_InvalidUrl_Returns404();

    void deleteAny_InvalidUrl_Returns404();

    // Validation::ShippingAddress
    void upsert_NullShippingAddress_Returns400ValidationError();

    void upsert_NullCountryShippingAddress_Returns400ValidationError();

    void upsert_NullCityShippingAddress_Returns400ValidationError();

    void upsert_NullStateShippingAddress_Returns400ValidationError();

    void upsert_NullStreetShippingAddress_Returns400ValidationError();

    void upsert_NullZipShippingAddress_Returns400ValidationError();

    void upsert_BlankCountryShippingAddress_Returns400ValidationError();

    void upsert_BlankCityShippingAddress_Returns400ValidationError();

    void upsert_BlankStateShippingAddress_Returns400ValidationError();

    void upsert_BlankStreetShippingAddress_Returns400ValidationError();

    void upsert_BlankZipShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100CountyShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen20ZipInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen50CountryInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100CityInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100StateInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen200StreetInShippingAddress_Returns400ValidationError();

    // Validation::BillingAddress

    void upsert_LengthGreaterThen100CountyBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen20ZipInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen50CountryInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100CityInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100StateInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen200StreetInBillingAddress_Returns400ValidationError();

    @Test
    @WithMockUser
    void upsert_PartialAddressWithNullState_Returns400ValidationError();

    @Test
    @WithMockUser
    void upsert_PartialAddressWithNullZip_Returns400ValidationError();

    // Validation::TransactionType
    void upsert_NullTransactionType_Returns400ValidationError();

    // Validation::Items
    void upsert_NullItemsList_Returns400ValidationError();

    void upsert_EmptyItemsList_Returns400ValidationError();

    // Validation::SalesTax
    void upsert_NegativeAmountInSalesTax_Returns400validationError();

    // Validation::CreatedFrom
    void upsert_LengthGreaterThan256CreatedFrom_Returns400validationError();

    //Validation::CustomerId
    void upsert_CustomerIdFailedToParse_Returns400();

    void upsert_NullCustomerId_Returns400();

    // Validation::Customer
    void upsert_InvalidCustomer_Returns400();

    void upsert_nullDocumentName_Returns200Ok();

    void upsert_blankDocumentName_Returns400();

    void upsert_moreThan50ChartsDocumentName_Returns400();

    void upsert_50ChartsDocumentName_Returns200Ok();

//    // Validation::Discount todo:fix
//    void upsert_TransactionWithNoDiscount_Return200();
//
//    void upsert_TransactionWithDiscountLessThanTotalAmount_Return200();
//
//    void upsert_TransactionWithPositiveDiscountAmount_Return200();
//
//    void upsert_TransactionWithDiscountEqualsToTotalAmount_Return200();
//
//    void upsert_TransactionWithDiscountLargerThanTotalAmount_Return400();
//
//    void upsert_TransactionWithDiscountNoDiscountAmount_Return400();
}
