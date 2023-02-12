package io.complyt.files.v1.routers;

import org.junit.jupiter.api.Test;
import testUtils.templates.endpoints.GetRouterTest;

public interface FileRouterTest extends
        GetRouterTest {
    @Test
    void getAny_InvalidUrl_Returns404();
}
