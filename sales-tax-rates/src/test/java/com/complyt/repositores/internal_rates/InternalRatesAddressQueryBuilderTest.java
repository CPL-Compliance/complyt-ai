//package com.complyt.repositores.internal_rates;
//
//import com.complyt.domain.internal_rates.InternalAddress;
//import com.complyt.repositories.internal_rates.InternalRatesAddressQueryBuilder;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import testUtils.TestUtilities;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@ExtendWith(MockitoExtension.class)
//class InternalRatesAddressQueryBuilderTest {
//    InternalAddress internalAddress;
//    InternalRatesAddressQueryBuilder internalRatesAddressQueryBuilder;
//
//    @BeforeEach
//    void setUp() {
//        internalAddress = TestUtilities.createInternalAddress();
//        internalRatesAddressQueryBuilder = new InternalRatesAddressQueryBuilder();
//    }
//
//    @Test
//    void build_ShouldReturnQueryWithCorrectCriteria() {
//        // Given
//        Criteria expectedCriteria = Criteria
//                .where("address.state").is(internalAddress.state())
//                .and("address.zip").is(internalAddress.zip())
//                .and("address.hasPlusFourZipCode").is(internalAddress.hasPlusFourZipCode())
//                .and("address.lowerPlusFourDigits").is(internalAddress.lowerPlusFourDigits())
//                .and("address.upperPlusFourDigits").is(internalAddress.upperPlusFourDigits())
//                .and("address.county").is(internalAddress.county())
//                .and("address.city").is(internalAddress.city())
//                .and("address.isUnincorporated").is(internalAddress.isUnincorporated());
//
//
//        Query expectedQuery = Query.query(expectedCriteria);
//
//        // When
//        Query actualQuery = internalRatesAddressQueryBuilder.build(internalAddress);
//
//        // Then
//        assertEquals(expectedQuery, actualQuery);
//    }
//}