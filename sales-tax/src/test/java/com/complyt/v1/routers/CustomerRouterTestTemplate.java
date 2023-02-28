package com.complyt.v1.routers;

import testUtils.templates.endpoints.*;
import testUtils.templates.validations.ExternalTimestampsValidationTestTemplate;
import testUtils.templates.validations.InternalTimestampsValidationTestTemplate;

public interface CustomerRouterTestTemplate extends
        GetByExternalIdAndSourceRouterTestTemplate,
        GetByComplytIdRouterTestTemplate,
        GetByNameRouterTestTemplate,
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

    // Validation::Address
    void upsert_NullAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CountyAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen20ZipInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CountryInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CityInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StateInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StreetInAddress_Returns400ValidationError();

    // Validation::CustomerType
    void upsert_NullCustomerType_Returns400ValidationError();

}
