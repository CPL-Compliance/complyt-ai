package integration.test_utils.templates.methods;

public interface PutITTemplate {

    void put_NoAccessToken_Returns401();

    void put_InsufficientScopes_Returns403();
}
