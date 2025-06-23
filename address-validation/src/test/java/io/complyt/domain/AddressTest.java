package io.complyt.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddressTest {

    @Test
    void AddressConstructor_UsaAddress_shouldKeepExplicitIsPartialTrue() {
        Address address = new Address("City", "USA", "County", "CA", "Street", "90210", null, true);
        assertThat(address.isPartial()).isTrue();
    }

    @Test
    void AddressConstructor_NonUsaAddress_shouldKeepExplicitIsPartialFalse() {
        Address address = new Address("City", "Canada", "County", "ON", "Street", "M5V", "Region", false);
        assertThat(address.isPartial()).isFalse();
    }

    @Test
    void AddressConstructor_UsaAddress_shouldInferIsPartialForUSWhenCityFieldMissing() {
        Address address = new Address(null, "USA", "County", "CA", "Street", "90210", null, null);
        assertThat(address.isPartial()).isTrue();
    }

    @Test
    void AddressConstructor_UsaAddress_shouldInferIsPartialForUSWhenStateFieldMissing() {
        Address address = new Address("City", "USA", "County", null, "Street", "90210", null, null);
        assertThat(address.isPartial()).isTrue();
    }

    @Test
    void AddressConstructor_UsaAddress_shouldInferIsPartialForUSWhenStreetFieldMissing() {
        Address address = new Address("City", "USA", "County", "State", null, "90210", null, null);
        assertThat(address.isPartial()).isTrue();
    }

    @Test
    void AddressConstructor_UsaAddress_shouldInferIsPartialForUSWhenZipFieldMissing() {
        Address address = new Address("City", "USA", "County", "State", "Street", null, null, null);
        assertThat(address.isPartial()).isTrue();
    }

    @Test
    void AddressConstructor_UsaAddress_shouldInferIsPartialForUSWhenAllFieldsPresent() {
        Address address = new Address("City", "USA", "County", "CA", "Street", "90210", null, null);
        assertThat(address.isPartial()).isFalse();
    }

    @Test
    void AddressConstructor_NonUsaAddress_shouldInferIsPartialForNonUSWhenRegionMissing() {
        Address address = new Address("City", "Germany", "County", "State", "Street", "12345", null, null);
        assertThat(address.isPartial()).isTrue();
    }

    @Test
    void AddressConstructor_NonUsaAddress_shouldInferIsPartialForNonUSWhenAllFieldsPresent() {
        Address address = new Address("City", "Germany", "County", "State", "Street", "12345", "Bavaria", null);
        assertThat(address.isPartial()).isFalse();
    }

    @Test
    void AddressConstructor_AddressMissingCountry_shouldInferIsPartialAsTrueIfCountryIsNull() {
        Address address = new Address("City", null, "County", "State", "Street", "12345", "Region", null);
        assertThat(address.isPartial()).isTrue();
    }
}
