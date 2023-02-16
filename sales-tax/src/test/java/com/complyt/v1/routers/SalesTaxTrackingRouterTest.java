package com.complyt.v1.routers;

import testUtils.templates.endpoints.GetAllRouterTest;
import testUtils.templates.endpoints.GetByComplytIdRouterTest;
import testUtils.templates.endpoints.GetByStateRouterTest;
import testUtils.templates.endpoints.UpsertByStateRouterTest;
import testUtils.templates.validations.StateValidationRouterTest;

public interface SalesTaxTrackingRouterTest extends
        GetAllRouterTest,
        GetByComplytIdRouterTest,
        GetByStateRouterTest,
        // Validation::StateName, ComplytId
        UpsertByStateRouterTest,
        // Validation::State
        StateValidationRouterTest {

    void getAny_InvalidUrl_Returns404();

    void putAny_InvalidUrl_Returns404();

    // Validation::PhysicalNexusTracker
    void upsert_NullPhysicalNexusTrackerDto_Returns400ValidationError();

    void upsert_NullEstablishedDatePhysicalNexusTrackerDto_Returns400ValidationError();

    // Validation::EconomicNexusTracker
    void upsert_NullEconomicNexusTrackerDto_Returns400ValidationError();

    void upsert_NullEstablishedDateEconomicNexusTrackerDto_Returns400ValidationError();
}
