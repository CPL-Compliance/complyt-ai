//package com.complyt.business.transaction.data_injector;
//
//import com.complyt.domain.transaction.Address;
//import com.complyt.domain.transaction.CityCountyWrapper;
//import com.complyt.domain.transaction.Transaction;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//import testUtils.unit_test.UnitTestUtilities;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionCityCountyInjectorTest {
//
//    @InjectMocks
//    TransactionMatchedAddressInjector transactionCityCountyInjector;
//
//    @Mock
//
//    Transaction transaction;
//
//    private UnitTestUtilities testUtilities;
//    Address address;
//    CityCountyWrapper cityCountyWrapper;
//
//     static MockedStatic mockedStatic;
//
//    @BeforeAll
//    static void beforeAll() {
//        try {
//            mockedStatic = mockStatic(TenantResolver.class);
//        } catch (Exception e) {
//            // Log the error or fail the test setup
//            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    @AfterAll
//    static void afterAll() {
//        mockedStatic.close();
//    }
//
//    @BeforeEach
//    void setUp() {
//        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
//        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
//        address = transaction.getShippingAddress().withCounty("New County");
//        cityCountyWrapper = new CityCountyWrapper(address.city(), address.county());
//        transactionCityCountyInjector = new TransactionMatchedAddressInjector();
//    }
//
//    @Test
//    void inject_DifferentCounty_TransitionModified() {
//        // Given
//        Address address = transaction.getShippingAddress().withCounty("New County");
//        Transaction expectedTransition = transaction.withShippingAddress(address);
//
//        // When
//        Mono<Transaction> transactionMono = transactionCityCountyInjector.inject(cityCountyWrapper, transaction);
//
//        // Then
//        StepVerifier.create(transactionMono).expectNext(expectedTransition).verifyComplete();
//    }
//
//    @Test
//    void inject_NullTransactionPassed_ThrowsNullPointerException() {
//        // Given
//        Transaction nullTransaction = null;
//
//        // When
//        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
//            transactionCityCountyInjector.inject(cityCountyWrapper, nullTransaction);
//        });
//
//        // Then
//        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
//    }
//
//}