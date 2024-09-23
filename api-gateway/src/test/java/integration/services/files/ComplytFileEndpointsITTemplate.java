package integration.services.files;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

public interface ComplytFileEndpointsITTemplate {
    @Order(1)
    @Test
    @WithMockUser
    void saveOneFile_Exists_Returns201();
}
