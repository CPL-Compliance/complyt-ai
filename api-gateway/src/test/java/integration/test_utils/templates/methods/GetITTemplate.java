package integration.test_utils.templates.methods;

public interface GetITTemplate {

    void get_NoAccessToken_Returns401();

    void get_InsufficientScopes_Returns403();
}
