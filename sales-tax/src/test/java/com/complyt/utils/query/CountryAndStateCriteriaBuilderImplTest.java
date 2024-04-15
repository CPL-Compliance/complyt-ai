package com.complyt.utils.query;

import com.complyt.business.address.SupportedNonUsCountries;
import com.complyt.business.address.UsaAbbreviations;
import com.complyt.domain.transaction.Address;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CountryAndStateCriteriaBuilderImplTest {

    private CountryAndStateCriteriaBuilderImpl criteriaBuilder;
    UnitTestUtilities testUtilities;

    @BeforeEach
    public void setUp() {
        criteriaBuilder = new CountryAndStateCriteriaBuilderImpl();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    private List<Criteria> listOfUsaAbbreviationsOptionsCriteria() { //todo: this is a duplicated code from nexustransactionsearchquerybuilder
        return UsaAbbreviations.usaAbbreviationsList.stream()
                .map(abbreviation -> Criteria.where("country").is(abbreviation.toUpperCase())).collect(Collectors.toList());
    }

    private List<Criteria> listOfNonUsaAbbreviationCriteria(@NonNull String country) {//todo: duplicated code almost
        return SupportedNonUsCountries.nonUsaCountriesAbbreviations.get(country.toUpperCase()).stream()
                .map(abbreviation -> Criteria.where("country").is(abbreviation.toUpperCase())).collect(Collectors.toList());
    }

    @Test
    public void build_BuildsCriteriaInUSAWithState_ReturnsCriteria() {
        // Given
        Address address = testUtilities.createUsaAddress();
        Criteria expectedCriteria = new Criteria().orOperator(listOfUsaAbbreviationsOptionsCriteria())
                .andOperator(new Criteria().orOperator(Criteria.where("state.abbreviation").is(address.state()), Criteria.where("state.name").is(address.state())));

        // When
        Criteria actualCriteria = criteriaBuilder.build(address);

        // Then
        Assertions.assertEquals(actualCriteria.toString(), expectedCriteria.toString());
    }

    @Test
    public void build_BuildsCriteriaNotInUSA_ReturnsCriteria() {
        // Given
        Address address = testUtilities.createNonUsaAddress();
        Criteria expectedCriteria = new Criteria().orOperator(listOfNonUsaAbbreviationCriteria(address.country().toUpperCase()));

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