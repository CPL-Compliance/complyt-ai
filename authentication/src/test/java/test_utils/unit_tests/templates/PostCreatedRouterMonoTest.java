package test_utils.unit_tests.templates;

import org.junit.jupiter.api.Test;

public interface PostCreatedRouterMonoTest {

    @Test
    void post_Exists_Returns201();

    @Test
    void post_DoesntExist_Returns401();

    @Test
    void post_InternalServerError_Returns500();

    @Test
    void post_NullHandler_ThrowsNullPointerException();

    @Test
    void post_UnsupportedMediaType_Returns415();

    @Test
    void rotate_NullHandler_ThrowsNullPointerException();

    @Test
    void rotate_SentAsURLEncoded_Exists_return201();

    @Test
    void rotate_SentAsJson_Exists_return201();

    @Test
    void rotate_DoesntExists_return401();

    @Test
    void rotate_InternalServerError_Returns500();

    @Test
    void rotate_UnsupportedMediaType_Returns415();


}
