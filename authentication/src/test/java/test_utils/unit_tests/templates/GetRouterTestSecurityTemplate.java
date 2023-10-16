package test_utils.unit_tests.templates;

import org.junit.jupiter.api.Test;

public interface GetRouterTestSecurityTemplate {
    @Test
    void get_UnauthenticatedUser_Returns401();
}
