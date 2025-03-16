package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.enums.RatesStatus;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.domain.mappers.InternalRatesToCommonRatesMapper;
import com.complyt.repositories.internal_rates.InternalSalesTaxRatesRepository;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.exceptions.types.ObjectNotValidApiException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternalSalesTaxRatesServiceImplTest {
    @InjectMocks
    InternalSalesTaxRatesServiceImpl internalSalesTaxRatesServiceImp;

    @Mock
    InternalSalesTaxRatesRepository internalSalesTaxRatesRepository;

    @Mock
    ComplytIdHandler<InternalSalesTaxRates> complytIdHandler;

    @Mock
    TaxRateApplicabilityProcessor taxRateApplicabilityProcessor;


    private final AddressWithDate addressWithDate = TestUtilities.createAddressInCaliforniaWithCreationDate();
    private final InternalSalesTaxRates internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now(), UUID.randomUUID());

    @Test
    void findByAddress_AddressFound_ReturnsAddress() {
        CommonSalesTaxRates expectedRate =  InternalRatesToCommonRatesMapper.INSTANCE.map(internalSalesTaxRates.withSalesTaxRates(internalSalesTaxRates.getSalesTaxRates()));

        // When
        when(internalSalesTaxRatesRepository.find(addressWithDate)).thenReturn(Mono.just(internalSalesTaxRates));
        when(taxRateApplicabilityProcessor.processRates(internalSalesTaxRates, addressWithDate.getEffectiveDate())).thenReturn(internalSalesTaxRates.getSalesTaxRates());


        Mono<CommonSalesTaxRates> result = internalSalesTaxRatesServiceImp.findByAddress(addressWithDate);

        StepVerifier.create(result)
                .expectNext(expectedRate)
                .verifyComplete();
    }


    @Test
    void findByAddress_AddressNotFound_ReturnsMonoEmpty() {
        // When
        when(internalSalesTaxRatesRepository.find(addressWithDate)).thenReturn(Mono.empty());

        Mono<CommonSalesTaxRates> result = internalSalesTaxRatesServiceImp.findByAddress(addressWithDate);

        StepVerifier.create(result)
                .verifyComplete();
    }

    @Test
    void save_WithValidInternalSalesTaxRates() {
        // Arrange
        InternalSalesTaxRates newInternalSalesTaxRates = new InternalSalesTaxRates(null, null, internalSalesTaxRates.getAddress(), internalSalesTaxRates.getSalesTaxRates(),
                internalSalesTaxRates.getEffectiveDates(), internalSalesTaxRates.getInternalSalesTaxRatesMetaData(), internalSalesTaxRates.getCreatedDate(), internalSalesTaxRates.getExpiredDate(), internalSalesTaxRates.getAppliedDate(), internalSalesTaxRates.getUpdatedFrom(), internalSalesTaxRates.getUpdatedTo());

        when(complytIdHandler.insertComplytIdToNew(any())).thenReturn(newInternalSalesTaxRates);
        when(internalSalesTaxRatesRepository.save(newInternalSalesTaxRates)).thenReturn(Mono.just(newInternalSalesTaxRates));

        // Act
        Mono<InternalSalesTaxRates> result = internalSalesTaxRatesServiceImp.save(internalSalesTaxRates);

        // Assert
        StepVerifier.create(result)
                .expectNext(newInternalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void save_NullTaxableLocation_ThrowsException() {
        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            internalSalesTaxRatesServiceImp.save(null);
        });

        assertEquals("internalSalesTaxRates is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void findByAddress_NullAddressWithDate_ThrowsException() {
        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            internalSalesTaxRatesServiceImp.findByAddress(null);
        });

        assertEquals("addressWithDate is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void updateRate_whenInvalidEnumValue_throwsException() {
        RatesStatus invalidStatus = mock(RatesStatus.class); // Create a fake enum value

        Mono<InternalSalesTaxRates> result = internalSalesTaxRatesServiceImp.updateRate(internalSalesTaxRates, invalidStatus);

        StepVerifier.create(result)
                .expectError(ObjectNotValidApiException.class)
                .verify();

    }

    @Test
    void updateRate_whenStatusIsNew_callsSaveMethod() {
        // Given
        when(internalSalesTaxRatesRepository.save(internalSalesTaxRates)).thenReturn(Mono.just(internalSalesTaxRates));
        when(complytIdHandler.insertComplytIdToNew(any())).thenReturn(internalSalesTaxRates);

        // When
        Mono<InternalSalesTaxRates> result = internalSalesTaxRatesServiceImp.updateRate(internalSalesTaxRates, RatesStatus.NEW);

        // Then
        StepVerifier.create(result)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void updateRate_whenStatusIsArchive_callsArchiveMethod() {
        // Given
        when(internalSalesTaxRatesRepository.archive(internalSalesTaxRates)).thenReturn(Mono.just(internalSalesTaxRates));

        // When
        Mono<InternalSalesTaxRates> result = internalSalesTaxRatesServiceImp.updateRate(internalSalesTaxRates, RatesStatus.ARCHIVE);

        // Then
        StepVerifier.create(result)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void updateRate_whenStatusIsUpdate_callsFindAndUpdateMethod() {
        // Given
        when(internalSalesTaxRatesRepository.updateRate(internalSalesTaxRates)).thenReturn(Mono.just(internalSalesTaxRates));
        when(complytIdHandler.insertComplytIdToNew(any())).thenReturn(internalSalesTaxRates);

        // When
        Mono<InternalSalesTaxRates> result = internalSalesTaxRatesServiceImp.updateRate(internalSalesTaxRates, RatesStatus.UPDATE);

        // Then
        StepVerifier.create(result)
                .expectNext(internalSalesTaxRates)
                .verifyComplete();
    }

    @Test
    void updateRate_whenInternalSalesTaxRatesIsNull_throwsException() {
        // Then
        assertThrows(NullPointerException.class, () -> internalSalesTaxRatesServiceImp.updateRate(null, RatesStatus.NEW));
    }

    @Test
    void updateRate_whenRatesStatusIsNull_throwsException() {
        // Then
        assertThrows(NullPointerException.class, () -> internalSalesTaxRatesServiceImp.updateRate(internalSalesTaxRates, null));
    }

    @Test
    void setBeforeSave_whenRatesStatusIsNull_throwsException() {
        // Then
        assertThrows(NullPointerException.class, () -> internalSalesTaxRatesServiceImp.setBeforeSave(null));
    }
}