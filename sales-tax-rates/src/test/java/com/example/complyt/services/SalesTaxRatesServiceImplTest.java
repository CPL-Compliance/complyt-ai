//package com.example.complyt.services;
//
//import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
//import com.complyt.business.sales_tax_web_clients.SalesTaxWebClientWrapper;
//import com.complyt.domain.Address;
//import com.complyt.domain.AddressWithSalesTaxRates;
//import com.complyt.domain.SalesTaxRates;
//import com.complyt.domain.StatesMap;
//import com.complyt.repositories.SalesTaxRatesRepository;
//import com.complyt.services.SalesTaxRatesServiceImpl;
//import com.testUtils.TestUtilities;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.time.LocalDateTime;
//
//import static org.mockito.Mockito.when;
//
//public class SalesTaxRatesServiceImplTest {
//
//    @InjectMocks
//    SalesTaxRatesServiceImpl salesTaxRatesService;
//
//    @Mock
//    SalesTaxRatesRepository salesTaxRatesRepository;
//
//    @Mock
//    SalesTaxWebClientWrapper salesTaxWebClientWrapper;
//
//    @Mock
//    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void findByAddress_FindsAddressWithRates_ReturnsRates() {
//        // Given
//        Address califoniaAddress = TestUtilities.createAddressInCalifornia();
//        String collectionName = StatesMap.statesToCollections.get(califoniaAddress.getState());
//        SalesTaxRates californiaRates = TestUtilities.createCaliforniaSalesTaxRates();
//
//        AddressWithSalesTaxRates addressWithSalesTaxRates =
//                new AddressWithSalesTaxRates(califoniaAddress, californiaRates, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
//
//        // When
//        when(salesTaxRatesRepository.findByAddress(califoniaAddress, collectionName)).thenReturn(Mono.just(addressWithSalesTaxRates));
//        Mono<SalesTaxRates> salesTaxRatesMono = salesTaxRatesService.findByAddress(califoniaAddress);
//
//        // Then
//        StepVerifier.create(salesTaxRatesMono).expectNext(californiaRates).verifyComplete();
//    }
//}
