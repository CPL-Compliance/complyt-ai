package com.complyt.business.transaction;

import com.complyt.business.transaction.data_checker.AddressValidationApplyChecker;
import com.complyt.business.transaction.data_fetcher.MatchedAddressFetcher;
import com.complyt.business.transaction.data_injector.TransactionMatchedAddressInjector;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.transaction.MatchedAddressData;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CityCountyProviderTest {

    @InjectMocks
    MatchedAddressProvider cityCountyProvider;

    @Mock
    MatchedAddressFetcher addressFetcher;

    @Mock
    TransactionMatchedAddressInjector transactionCityCountyInjector;

    @Mock
    AddressValidationApplyChecker addressValidationApplyChecker;

    UnitTestUtilities testUtilities;
    SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo;
    Transaction transaction;
    MatchedAddressData matchedAddressData;

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

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(testUtilities.createSalesTaxTracking("123"), false);
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        matchedAddressData = UnitTestUtilities.createMatchedAddressData();
    }

    @Test
    void provide_GetsAddressAndInjectsIt_ReturnsTransaction() {
        // When
        when(addressValidationApplyChecker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo)).thenReturn(true);
        when(addressFetcher.fetch(transaction.getShippingAddress())).thenReturn(Mono.just(matchedAddressData));
        when(transactionCityCountyInjector.inject(matchedAddressData, transaction)).thenReturn(Mono.just(transaction));
        Mono<Transaction> transactionMono = cityCountyProvider.provide(transaction, salesTaxTrackingWithNexusInfo);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void provide_ShouldNotValidateAddress_ReturnsOriginalTransaction() {
        // Mock shouldValidateAddress to return false
        when(addressValidationApplyChecker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo)).thenReturn(false);

        // Test the provide method
        StepVerifier.create(cityCountyProvider.provide(transaction, salesTaxTrackingWithNexusInfo))
                .expectNext(transaction)
                .verifyComplete();
    }

}