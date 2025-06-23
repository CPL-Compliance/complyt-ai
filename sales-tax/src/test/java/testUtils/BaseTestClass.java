package testUtils;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void beforeEach() {
        when(TenantResolver.resolve()).thenReturn(Mono.empty());
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }
}
