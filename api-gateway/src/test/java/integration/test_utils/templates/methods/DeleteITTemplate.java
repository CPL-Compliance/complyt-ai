package integration.test_utils.templates.methods;

public interface DeleteITTemplate {

    void delete_NoAccessToken_Returns401();

    void delete_InsufficientScopes_Returns403();
}
