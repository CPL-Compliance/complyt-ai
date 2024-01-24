package com.complyt.v1.routers;

import testUtils.unit_test.templates.endpoints.GetAllRouterTestTemplate;
import testUtils.unit_test.templates.endpoints.GetByComplytIdRouterTestTemplate;
import testUtils.unit_test.templates.endpoints.GetByStateRouterTestTemplate;
import testUtils.unit_test.templates.endpoints.UpsertByStateRouterTestTemplate;
import testUtils.unit_test.templates.validations.StateValidationTestTemplate;

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

    void postAny_InvalidUrl_Returns404();

    // Validation::PhysicalNexusTracker
    void upsert_NullPhysicalNexusTrackerDto_Returns400ValidationError();

    void upsert_NullEstablishedDatePhysicalNexusTrackerDto_Returns400ValidationError();

    // Validation::EconomicNexusTracker
    void upsert_NullEconomicNexusTrackerDto_Returns400ValidationError();

    void upsert_NullEstablishedDateEconomicNexusTrackerDto_Returns400ValidationError();

    // Validation: Comment
    void upsert_LengthGreaterThen200Comment_Returns400ValidationError();

    void upsert_NewWithBlankComment_Returns201();

    //     Refresh By State And Date
    void refreshByStateAndDate_ReturnsSalesTaxTracking_Returns200();

    void refreshByStateAndDate_FacadeReturnsEmpty_Returns404NotFound();

    void refreshByStateAndDate_DateNotInFormat_Returns400();

    void refreshByStateAndDate_NoDateInAsQueryParam_Returns400();

    void refreshByStateAndDate_UnauthenticatedUser_Returns401();

    void refreshByStateAndDate_UserWithoutAuthorities_Returns403();

    void refreshByStateAndDate_UserWithoutCSRFToken_Returns403();

    void refreshByStateAndDate_PathVariableError_Returns400();

    void refreshByStateAndDate_InternalServerError_Returns500();

    void refreshByStateAndDate_NullHandler_ThrowsNullPointerException();
}
