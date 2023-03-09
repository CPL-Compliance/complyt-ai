package com.complyt.v1.routers;

import testUtils.templates.endpoints.*;
import testUtils.templates.validations.ExternalTimestampsValidationTestTemplate;
import testUtils.templates.validations.InternalTimestampsValidationTestTemplate;
import testUtils.templates.validations.ItemValidationTestTemplate;
import testUtils.templates.validations.ShippingFeeValidationTestTemplate;

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

    void upsert_LengthGreaterThen256CountyShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen20ZipInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CountryInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CityInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StateInShippingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StreetInShippingAddress_Returns400ValidationError();

    // Validation::BillingAddress
    void upsert_NullBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CountyBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen20ZipInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CountryInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CityInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StateInBillingAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StreetInBillingAddress_Returns400ValidationError();

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
