package com.complyt.v1.routers;

import org.junit.jupiter.api.Test;
import testUtils.templates.endpoints.*;
import testUtils.templates.validations.ExternalTimestampsValidationRouterTest;
import testUtils.templates.validations.InternalTimestampsValidationRouterTest;

public interface CustomerRouterTest extends
        GetByExternalIdAndSourceRouterTest,
        GetByComplytIdRouterTest,
        GetByNameRouterTest,
        GetAllRouterTest,
        GetAllBySourceRouterTest,
        // Validation::ExternalId, Source, ComplytId
        UpsertByExternalIdAndSourceRouterTest,
        // Validation::InternalTimestamps
        InternalTimestampsValidationRouterTest,
        // Validation::ExternalTimestamps
        ExternalTimestampsValidationRouterTest {
    void getAny_InvalidUrl_Returns404();

    void putAny_InvalidUrl_Returns404();

    // Validation::Name
    void upsert_BlankName_Returns400ValidationError();

    void upsert_LengthGreaterThen256Name_Returns400ValidationError();

    // Validation::Address
    void upsert_NullAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CountyAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen10ZipInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CountryInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256CityInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StateInAddress_Returns400ValidationError();

    void upsert_LengthGreaterThen256StreetInAddress_Returns400ValidationError();

    // Validation::CustomerType
    void upsert_NullCustomerType_Returns400ValidationError();

}
