package com.complyt.v1.routers;

import testUtils.ut.templates.endpoints.*;
import testUtils.ut.templates.validations.ExternalTimestampsValidationTestTemplate;
import testUtils.ut.templates.validations.InternalTimestampsValidationTestTemplate;
import testUtils.ut.templates.validations.ItemValidationTestTemplate;
import testUtils.ut.templates.validations.ShippingFeeValidationTestTemplate;

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

    void upsert_BlankCountyShippingAddress_Returns400ValidationError();

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

    void upsert_BlankCountryBillingAddress_Returns400ValidationError();

    void upsert_BlankCountyBillingAddress_Returns400ValidationError();

    void upsert_BlankCityBillingAddress_Returns400ValidationError();

    void upsert_BlankStateBillingAddress_Returns400ValidationError();

    void upsert_BlankStreetBillingAddress_Returns400ValidationError();

    void upsert_BlankZipBillingAddress_Returns400ValidationError();

    // Validation::TransactionType
    void upsert_NullTransactionType_Returns400ValidationError();

    // Validation::Items
    void upsert_NullItemsList_Returns400ValidationError();

    void upsert_EmptyItemsList_Returns400ValidationError();

    // Validation::SalesTax
    void upsert_NegativeAmountInSalesTax_Returns400validationError();

    // Validation::CreatedFrom
    void upsert_LengthGreaterThan256CreatedFrom_Returns400validationError();

    void upsert_BlankCreatedFrom_Returns400validationError();

    //Validation::CustomerId
    void upsert_CustomerIdFailedToParse_Returns400();

    void upsert_NullCustomerId_Returns400();

    // Validation::Customer
    void upsert_InvalidCustomer_Returns400();

}
