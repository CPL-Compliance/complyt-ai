package com.complyt.v1.routers;

import testUtils.unit_test.templates.endpoints.*;
import testUtils.unit_test.templates.validations.ExternalTimestampsValidationTestTemplate;
import testUtils.unit_test.templates.validations.InternalTimestampsValidationTestTemplate;

public interface CustomerRouterTestTemplate extends
        GetByExternalIdAndSourceRouterTestTemplate,
        GetByComplytIdRouterTestTemplate,
        GetAllRouterTestTemplate,
        GetAllBySourceRouterTestTemplate,
        // Validation::ExternalId, Source, ComplytId
        UpsertByExternalIdAndSourceRouterTestTemplate,
        // Validation::InternalTimestamps
        InternalTimestampsValidationTestTemplate,
        // Validation::ExternalTimestamps
        ExternalTimestampsValidationTestTemplate {
    void getAny_InvalidUrl_Returns404();

    void putAny_InvalidUrl_Returns404();

    // Validation::Name
    void upsert_BlankName_Returns400ValidationError();

    void upsert_LengthGreaterThen256Name_Returns400ValidationError();

    // Validation::email
    void upsert_BlankEmail_Returns201Created();

    void upsert_NotInFormatEmail_Returns400ValidationError();
    void upsert_LengthGreaterThen100Email_Returns400ValidationError();

    // Validation::Address

    void upsert_LengthGreaterThen100CountyAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen20ZipInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen50CountryInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100CityInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100StateInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen200StreetInAddress_Returns400ValidationError();

    // Validation::CustomerType
    void upsert_NullCustomerType_Returns400ValidationError();

}
