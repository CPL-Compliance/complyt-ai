package com.complyt.v1.routers;

import testUtils.templates.endpoints.GetAllRouterTestTemplate;
import testUtils.templates.endpoints.GetByComplytIdRouterTestTemplate;
import testUtils.templates.endpoints.GetByStateRouterTestTemplate;
import testUtils.templates.endpoints.UpsertByStateRouterTestTemplate;
import testUtils.templates.validations.StateValidationTestTemplate;

public interface SalesTaxTrackingRouterTestTemplate extends
        GetAllRouterTestTemplate,
        GetByComplytIdRouterTestTemplate,
        GetByStateRouterTestTemplate,
        // Validation::StateName, ComplytId
        UpsertByStateRouterTestTemplate,
        // Validation::State
        StateValidationTestTemplate {

    void getAny_InvalidUrl_Returns404();

    void putAny_InvalidUrl_Returns404();

    // Validation::PhysicalNexusTracker
    void upsert_NullPhysicalNexusTrackerDto_Returns400ValidationError();

    void upsert_NullEstablishedDatePhysicalNexusTrackerDto_Returns400ValidationError();

    // Validation::EconomicNexusTracker
    void upsert_NullEconomicNexusTrackerDto_Returns400ValidationError();

    void upsert_NullEstablishedDateEconomicNexusTrackerDto_Returns400ValidationError();
}
