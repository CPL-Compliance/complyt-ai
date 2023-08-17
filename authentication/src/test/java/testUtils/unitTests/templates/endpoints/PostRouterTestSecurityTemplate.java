package testUtils.unitTests.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface PostRouterTestSecurityTemplate {
    @Test
    void post_UnauthenticatedUser_Returns401();

    @Test
    void post_missingCsrfToken_return403();
}
