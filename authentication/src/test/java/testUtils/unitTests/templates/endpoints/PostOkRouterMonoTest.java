package testUtils.unitTests.templates.endpoints;

import org.junit.jupiter.api.Test;

public interface PostOkRouterMonoTest {

    @Test
    void post_Exists_Returns200();

    @Test
    void post_DoesntExist_Returns404();

    @Test
    void post_InternalServerError_Returns500();

    @Test
    void post_NullHandler_ThrowsNullPointerException();
}
