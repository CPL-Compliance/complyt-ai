package com.complyt.v1.controllers.router;

import com.complyt.facades.ExemptionFacade;
import com.complyt.repositories.ExemptionRepository;
import com.complyt.services.ExemptionService;
import com.complyt.services.ExemptionServiceImpl;
import com.complyt.v1.controllers.router.handler.ExemptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExemptionRouterTest {

    ExemptionRouter exemptionRouter;

    ExemptionService exemptionService;

    @Test
    void exemptionRoute_nullExemptionHandler_ThrowsNullPointerException() {
        // Given
        ExemptionHandler nullExemptionHandler = null;
        exemptionRouter = new ExemptionRouter();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionRouter.exemptionsRoute(nullExemptionHandler);
        });

        // Then
        assertEquals("exemptionHandler is marked non-null but is null", nullPointerException.getMessage());
    }
    /*@Test void exemptionRoute_ExemptionHandler_ReturnRouterFunction() {
        // Given
        ExemptionHandler nullExemptionHandler = new ExemptionHandler(new ExemptionFacade(exemptionService));
        exemptionRouter = new ExemptionRouter();

        // When

        // Then
    } */
}
