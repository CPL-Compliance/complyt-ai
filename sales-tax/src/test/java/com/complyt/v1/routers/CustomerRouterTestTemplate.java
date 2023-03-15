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

    void upsert_LengthGreaterThen100CountyAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen20ZipInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen50CountryInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100CityInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen100StateInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen200StreetInAddress_Returns400ValidationError();

    void upsert_BlankCountryAddress_Returns400ValidationError();

    void upsert_BlankCountyAddress_Returns400ValidationError();

    void upsert_BlankCityAddress_Returns400ValidationError();

    void upsert_BlankStateAddress_Returns400ValidationError();

    void upsert_BlankStreetAddress_Returns400ValidationError();

    void upsert_BlankZipAddress_Returns400ValidationError();

    // Validation::CustomerType
    void upsert_NullCustomerType_Returns400ValidationError();

}
