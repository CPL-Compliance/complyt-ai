package testUtils.it.templates.endpoints;

public interface DeleteByExternalIdAndSourceITTemplate {
    void deleteByExternalIdAndSource_Exists_Returns204();

    void deleteByExternalIdAndSource_DoesntExists_Returns404();

    void deleteByExternalIdAndSource_UnauthenticatedUser_Returns401();

    void deleteByExternalIdAndSource_UserWithoutAuthorities_Returns403();

    void deleteByExternalIdAndSource_UserWithoutCSRFToken_Returns403();

    void deleteByExternalIdAndSource_InternalServerError_Returns500();

    void deleteByExternalIdAndSource_NullHandler_ThrowsNullPointerException();
}

