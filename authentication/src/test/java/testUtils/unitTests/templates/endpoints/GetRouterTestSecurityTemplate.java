package testUtils.unitTests.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface GetRouterTestSecurityTemplate {
    @Test
    void get_UnauthenticatedUser_Returns401();
}
