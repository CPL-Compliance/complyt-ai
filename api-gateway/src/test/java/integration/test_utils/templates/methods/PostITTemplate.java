package integration.test_utils.templates.methods;

public interface PostITTemplate {

    void post_NoAccessToken_Returns401();

    void post_InsufficientScopes_Returns403();
}
