package com.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {
    private final String city = "City";
    private final String country = "Country";
    private final String county = "County";
    private final String state = "State";
    private final String street = "Street";
    private final String zip = "ZIP";
    private Address address;
    private Address referenceAddress;

    @BeforeEach
    void setUp() {
        address = new Address("City", "Country", "County", "State", "Street", "ZIP", false);
        referenceAddress = new Address("City", "Country", "County", "State", "Street", "ZIP", false);
    }

    @Test
    void equals_EqualAddressValues_Equal() {
        assertEquals(address, referenceAddress);
    }

    @Test
    void canEqual_BothAddress_Equal() {
        assertEquals(address, referenceAddress);
    }

    @Test
    void hashCode_IdenticalAddresses_Equal() {
        assertEquals(referenceAddress.hashCode(), address.hashCode());
    }

    @Test
    void getCity_City_Equal() {
        assertEquals(city, address.city());
    }

    @Test
    void getCountry_Country_Equal() {
        assertEquals(country, address.country());
    }

    @Test
    void getCounty_County_Equal() {
        assertEquals(county, address.county());
    }

    @Test
    void getState_ValueIsState_Equal() {
        assertEquals(state, address.state());
    }

    @Test
    void getStreet_ValueIsStreet_Equal() {
        assertEquals(street, address.street());
    }

    @Test
    void getZip_ValueIsZip_Equal() {
        assertEquals(zip, address.zip());
    }

    @Test
    void toString_SameStrings_Equal() {
        boolean isPartial = false;
        String referenceString = "Address[city=" + city +
                ", country=" + country +
                ", county=" + county +
                ", state=" + state +
                ", street=" + street +
                ", zip=" + zip +
                ", isPartial=" + isPartial + "]";

        assertEquals(referenceString, address.toString());
    }

    @Test
    void withCity_SameValue_Equal() {
        String newCity = "New City";
        referenceAddress = address.withCity(newCity);

        assertEquals(referenceAddress.city(), newCity);
    }

    @Test
    void withCountry_SameValue_Equal() {
        String newCountry = "New Country";
        referenceAddress = address.withCountry(newCountry);

        assertEquals(referenceAddress.country(), newCountry);
    }

    @Test
    void withCounty_SameValue_Equal() {
        String newCounty = "New County";
        referenceAddress = address.withCounty(newCounty);

        assertEquals(referenceAddress.county(), newCounty);
    }

    @Test
    void withState_SameValue_Equal() {
        String newState = "New State";
        referenceAddress = address.withState(newState);

        assertEquals(referenceAddress.state(), newState);
    }

    @Test
    void withStreet_SameValue_Equal() {
        String newStreet = "New Street";
        referenceAddress = address.withStreet(newStreet);

        assertEquals(referenceAddress.street(), newStreet);
    }

    @Test
    void withZip_SameValue_Equal() {
        String newZip = "New ZIP";
        referenceAddress = address.withZip(newZip);

        assertEquals(referenceAddress.zip(), newZip);
    }
}