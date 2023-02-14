package com.complyt.v1.routers;

import testUtils.templates.endpoints.*;
import testUtils.templates.validations.InternalTimestampsValidationRouterTest;
import testUtils.templates.validations.StateValidationRouterTest;
import testUtils.templates.validations.ValidationDatesValidationRouterTest;

public interface ExemptionRouterTest extends
        GetByComplytIdRouterTest,
        GetAllRouterTest,
        DeleteByComplytIdRouterTest,
        CreateRouterTest,
        // Validations::ComplytId
        UpsertByComplytIdRouterTest,
        // Validations::InternalTimestamps
        InternalTimestampsValidationRouterTest,
        // Validations:ValidationDates
        ValidationDatesValidationRouterTest,
        // Validations::State
        StateValidationRouterTest {
    void getAny_InvalidUrl_Returns404();

    void putAny_InvalidUrl_Returns404();

    void deleteAny_InvalidUrl_Returns404();

    void postAny_InvalidUrl_Returns404();

    // Validations::Classification
    void upsert_NullClassification_Returns400ValidationError();

    void upsert_BlankCodeInClassification_Returns400ValidationError();

    void upsert_BlankDescriptionInClassification_Returns400ValidationError();

    void upsert_NullCodeInClassification_Returns400ValidationError();

    void upsert_NullDescriptionInClassification_Returns400ValidationError();

    void upsert_LengthOf257CodeInClassification_Returns400ValidationError();

    void upsert_LengthOf257DescriptionInClassification_Returns400ValidationError();

    // Validations::Status
    void upsert_NullStatus_Returns400validationError();

    void upsert_NullCodeInStatus_Returns400validationError();

    void upsert_NullNameInStatus_Returns400validationError();

    void upsert_blankCodeInStatus_Returns400validationError();

    void upsert_blankNameInStatus_Returns400validationError();

    void upsert_LengthOf257NameInStatus_Returns400validationError();

    void upsert_LengthOf257CodeInStatus_Returns400validationError();

    //Validations::Certificate
    void upsert_NullCertificate_Returns400ValidationError();

    void upsert_NullCertificateIdInCertificate_Returns400ValidationError();

    void upsert_NullUrlInCertificate_Returns400ValidationError();

    void upsert_NullNameInCertificate_Returns400ValidationError();

    void upsert_BlankCertificateIdInCertificate_Returns400ValidationError();

    void upsert_BlankUrlInCertificate_Returns400ValidationError();

    void upsert_BlankNameInCertificate_Returns400ValidationError();

    void upsert_LengthOf257CertificateIdInCertificate_Returns400ValidationError();

    void upsert_LengthOf257UrlInCertificate_Returns400ValidationError();

    void upsert_LengthOf257NameInCertificate_Returns400ValidationError();

    // Validations::ExemptionType
    void upsert_NullExemptionType_Returns400ValidationError();
}
