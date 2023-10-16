package test_utils.unit_tests.templates;

import org.junit.jupiter.api.Test;

public interface GetRouterTestMonoTemplate {
    @Test
    void get_Exists_Returns200();

    @Test
    void get_InternalServerError_Returns500();

    @Test
    void get_NullHandler_ThrowsNullPointerException();
}

