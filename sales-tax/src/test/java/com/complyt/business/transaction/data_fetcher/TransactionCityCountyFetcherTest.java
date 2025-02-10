package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.transaction.*;
import com.complyt.v1.mappers.MatchedAddressMapper;
import com.complyt.v1.models.matched_address.MatchedAddressDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionCityCountyFetcherTest {

    @InjectMocks
    private TransactionMatchedAddressFetcher transactionCityCountyFetcher;
    @Mock
    AddressValidationWebClientWrapper<MatchedAddressDataDto> addressAddressValidationWebClientWrapper;

    UnitTestUtilities testUtilities;
    private Transaction transaction;
    MatchedAddressDataDto matchedAddressDataDto;
    MatchedAddressData matchedAddressData;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        matchedAddressDataDto = testUtilities.createMatchedAddressDto();
        matchedAddressData = MatchedAddressMapper.INSTANCE.matchedAddressDataDtoToMatchedAddress(matchedAddressDataDto);
    }

    @Test
    void inject_InjectsCounty_ReturnsTransaction() {
        // Given
        ShippingAddress address = transaction.getShippingAddress();

        // When
        when(addressAddressValidationWebClientWrapper.validateAddress(address)).thenReturn(Mono.just(matchedAddressDataDto));
        Mono<MatchedAddressData> cityCountyWrapperMono = transactionCityCountyFetcher.fetch(address);

        // Then
        StepVerifier.create(cityCountyWrapperMono).expectNext(matchedAddressData).verifyComplete();
    }

}
