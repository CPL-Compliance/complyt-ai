package com.complyt.utils.query;

import com.complyt.business.address.SupportedNonUsCountries;
import com.complyt.domain.transaction.Address;
import com.complyt.security.TenantResolver;
import lombok.NonNull;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.springframework.data.mongodb.core.query.Criteria;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

public class CountryAndStateCriteriaBuilderImplTest {

    private CountryAndStateCriteriaBuilderImpl criteriaBuilder;
    UnitTestUtilities testUtilities;

   

    @BeforeEach
    public void setUp() {
        criteriaBuilder = new CountryAndStateCriteriaBuilderImpl();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    private Criteria usaAbbreviationsOptionsCriteria() {
        return Criteria.where("country").is("USA");
    }

    private Criteria nonUsaAbbreviationCriteria(@NonNull String country) {//todo: duplicated code almost
        String searchTermCountry = SupportedNonUsCountries.nonUsaCountriesAbbreviations.get(country.toUpperCase());

        return Criteria.where("country").is(searchTermCountry);
    }

    @Test
    public void build_BuildsCriteriaInUSAWithState_ReturnsCriteria() {
        // Given
        Address address = testUtilities.createUsaAddress();
        Criteria expectedCriteria = new Criteria().andOperator(new Criteria().orOperator(Criteria.where("state.abbreviation")
                .is(address.state()), Criteria.where("state.name").is(address.state())), usaAbbreviationsOptionsCriteria());

        // When
        Criteria actualCriteria = criteriaBuilder.build(address);

        // Then
        Assertions.assertEquals(expectedCriteria, actualCriteria);
    }

    @Test
    public void build_BuildsCriteriaNotInUSA_ReturnsCriteria() {
        // Given
        Address address = testUtilities.createNonUsaAddress();
        Criteria expectedCriteria = nonUsaAbbreviationCriteria(address.country().toUpperCase());

        // When
        Criteria actualCriteria = criteriaBuilder.build(address);

        // Then
        Assertions.assertEquals(actualCriteria.toString(), expectedCriteria.toString());
    }

    @Test
    public void build_NullAddressPassed_ThrowsNullPointerException() {
        // Given
        Address nullAddress = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            criteriaBuilder.build(nullAddress);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "address is marked non-null but is null");
    }

    @Test
    public void build_NullCountryPassed_ThrowsNullPointerException() {
        // Given
        String nullCountry = null;
        String state = "CA";

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            criteriaBuilder.build(nullCountry, state);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "country is marked non-null but is null");
    }
}