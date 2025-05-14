package test_utils;

import io.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

public abstract class BaseTestClass {
    static MockedStatic mockedStatic;
    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }
}
