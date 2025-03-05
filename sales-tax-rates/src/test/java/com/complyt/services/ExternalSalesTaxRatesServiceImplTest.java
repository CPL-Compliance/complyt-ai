package com.complyt.services;

import com.complyt.business.collection_fetcher.UsaStatesMap;
import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.data_fetcher.CityCountyFetcher;
import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.AddressWithDate;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.enums.RatesStatus;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.mappers.ComplytSalesTaxRatesToCommonRatesMapper;
import com.complyt.repositories.ComplytSalesTaxRatesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.AccessException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExternalSalesTaxRatesServiceImplTest {

    @InjectMocks
    ExternalSalesTaxRatesServiceImpl<ComplytSalesTaxRates> externalSalesTaxRatesServiceImpl;

    @Mock
    ComplytSalesTaxRatesRepository complytSalesTaxRatesRepository;

    @Mock
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Mock
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @Mock
    CityCountyFetcher cityCountyFetcher;

    @Mock
    ComplytIdHandler<ComplytSalesTaxRates> complytIdHandler;


    @Test
    void findByAddress_FindsComplytSalesTaxRates_ReturnsRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        AddressWithDate address = new AddressWithDate(califoniaAddress, LocalDateTime.now());

        String collectionName = UsaStatesMap.statesToCollections.get(califoniaAddress.state().toUpperCase());

        ComplytSalesTaxRates repositoryFetchedComplytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        CommonSalesTaxRates expectedComplytSalesTaxRate = ComplytSalesTaxRatesToCommonRatesMapper.INSTANCE.map(repositoryFetchedComplytSalesTaxRates);


        // When
        when(complytSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.just(repositoryFetchedComplytSalesTaxRates));
        Mono<CommonSalesTaxRates> complytSalesTaxRatesMono = externalSalesTaxRatesServiceImpl.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expectedComplytSalesTaxRate).verifyComplete();
    }


    @Test
    void findByAddress_ComplytSalesTaxRatesNotFoundInDB_SavesNewComplytSalesTaxRates() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        AddressWithDate address = new AddressWithDate(califoniaAddress, LocalDateTime.now());

        String collectionName = UsaStatesMap.statesToCollections.get(califoniaAddress.state().toUpperCase());
        SalesTaxRates californiaRates = TestUtilities.createCaliforniaSalesTaxRates();
        ComplytSalesTaxRates expectedComplytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        CommonSalesTaxRates expectedCommonRates = ComplytSalesTaxRatesToCommonRatesMapper.INSTANCE.map(expectedComplytSalesTaxRates);

        FastTaxGetBestMatchData fastTaxGetBestMatchData = TestUtilities.createFastTaxGetBestMatchData();

        // When
        when(complytSalesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.empty());
        when(salesTaxWebClientWrapper.findByAddress(califoniaAddress)).thenReturn(Mono.just(fastTaxGetBestMatchData));
        when(complytIdHandler.insertComplytIdToNew(any())).thenReturn(expectedComplytSalesTaxRates);
        when(complytSalesTaxRatesRepository.save(any(), any())).thenReturn(Mono.just(expectedComplytSalesTaxRates));
        when(salesTaxDataToSalesTaxRate.map(fastTaxGetBestMatchData)).thenReturn(Mono.just(californiaRates));

        Mono<CommonSalesTaxRates> complytSalesTaxRatesMono = externalSalesTaxRatesServiceImpl.findByAddress(address);

        // Then
        StepVerifier.create(complytSalesTaxRatesMono).expectNext(expectedCommonRates).verifyComplete();
    }


    @Test
    void findByAddress_NullAddressPassed_ThrowsException() {
        // Given
        AddressWithDate nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            externalSalesTaxRatesServiceImpl.findByAddress(nullAddress);
        });

        assertEquals("addressWithDate " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE, nullPointerException.getMessage());
    }

    @Test
    void save_NullComplytSalesTaxRatesPassed_ThrowsException() {
        // Given
        ComplytSalesTaxRates nullComplytSalesTaxRates = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            externalSalesTaxRatesServiceImpl.save(nullComplytSalesTaxRates);
        });

        assertEquals(nullPointerException.getMessage(), "complytSalesTaxRates " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void findByAddress_RepositoryThrowsException_ThrowsException() {
        // Given
        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
        AddressWithDate address = new AddressWithDate(califoniaAddress, LocalDateTime.now());

        String collection = UsaStatesMap.statesToCollections.get(califoniaAddress.state().toUpperCase());
        when(complytSalesTaxRatesRepository.findByAddress(califoniaAddress, collection)).thenThrow(RuntimeException.class);

        // When + Then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            externalSalesTaxRatesServiceImpl.findByAddress(address);
        });

        assertEquals(RuntimeException.class, runtimeException.getClass());
    }

    @Test
    void save_saveComplytSalesTaxRate_ReturnsComplytSaledTaxRate() {
        // Given
        ComplytSalesTaxRates complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        String collection = UsaStatesMap.statesToCollections.get(complytSalesTaxRates.getAddress().state().toUpperCase());

        // When
        when(complytSalesTaxRatesRepository.save(complytSalesTaxRates, collection)).thenReturn(Mono.just(complytSalesTaxRates));

        // Then
        StepVerifier.create(externalSalesTaxRatesServiceImpl.save(complytSalesTaxRates)).expectNext(complytSalesTaxRates).verifyComplete();
    }

    @Test
    void updateRate_whenInternalSalesTaxRatesIsNull_throwsException() {
        // Then
        assertThrows(NullPointerException.class, () -> externalSalesTaxRatesServiceImpl.updateRate(null, RatesStatus.NEW));
    }

    @Test
    void updateRate_whenRatesStatusIsNull_throwsException() {
        // Then
        ComplytSalesTaxRates complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        assertThrows(NullPointerException.class, () -> externalSalesTaxRatesServiceImpl.updateRate(complytSalesTaxRates, null));
    }

    @Test
    void updateRate_alwaysThrowsAccessException() {
        // When
        ComplytSalesTaxRates complytSalesTaxRates = TestUtilities.createCaliforniaComplytSalesTaxRates();
        Mono<ComplytSalesTaxRates> result = externalSalesTaxRatesServiceImpl.updateRate(complytSalesTaxRates, RatesStatus.NEW);

        // Then
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof AccessException &&
                        throwable.getMessage().equals("Endpoint Not Accessible"))
                .verify();
    }
}
