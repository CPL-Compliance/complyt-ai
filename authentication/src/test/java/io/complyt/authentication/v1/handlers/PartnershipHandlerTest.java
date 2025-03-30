package io.complyt.authentication.v1.handlers;

import io.complyt.authentication.facades.PartnershipFacade;
import io.complyt.authentication.v1.models.PartnershipDto;
import io.complyt.authentication.v1.validators.ValidationHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class PartnershipHandlerTest {

    @InjectMocks
    PartnershipHandler partnershipHandler;

    @Mock
    PartnershipFacade partnershipFacade;

    @Mock
    ValidationHandler<PartnershipDto, SpringValidatorAdapter> partnershipDtoValidationHandler;

    @Test
    void getPartnership_serverRequestIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipHandler.getPartnership(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "serverRequest is marked non-null but is null");
    }

    @Test
    void postNewReferral_serverRequestIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipHandler.upsertReferral(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "serverRequest is marked non-null but is null");
    }

    @Test
    void deleteReferral_serverRequestIsNull_throwsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            partnershipHandler.deleteReferral(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "serverRequest is marked non-null but is null");
    }
}