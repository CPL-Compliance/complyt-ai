package com.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class AddressTest {
    private Address address;
    private Address referenceAddress;
    private String city = "City";
    private String country = "Country";
    private String county = "County";
    private String state = "State";
    private String street = "Street";
    private String zip = "ZIP";

    @BeforeEach
    void setUp() {
        address = new Address("City", "Country", "County", "State", "Street", "ZIP");
        referenceAddress = new Address("City", "Country", "County", "State", "Street", "ZIP");
    }

    @Test
    void equals_EqualAddressValues_Equal() {
        assertTrue(address.equals(referenceAddress));
    }

    @Test
    void canEqual_BothAddress_Equal() {
        assertTrue(address.canEqual(referenceAddress));
    }

    @Test
    void hashCode_IdenticalAddresses_Equal() {
        assertEquals(referenceAddress.hashCode(), address.hashCode());
    }

    @Test
    void getCity_City_Equal() {
        assertEquals(city, address.getCity());
    }

    @Test
    void getCountry_Country_Equal() {
        assertEquals(country, address.getCountry());
    }

    @Test
    void getCounty_County_Equal() {
        assertEquals(county, address.getCounty());
    }

    @Test
    void getState_ValueIsState_Equal() {
        assertEquals(state, address.getState());
    }

    @Test
    void getStreet_ValueIsStreet_Equal() {
        assertEquals(street, address.getStreet());
    }

    @Test
    void getZip_ValueIsZip_Equal() {
        assertEquals(zip, address.getZip());
    }

    @Test
    void toString_SameStrings_Equal() {
        String referenceString = "Address(city=" + city + ", country=" + country + ", county=" + county + ", state=" + state + ", street=" + street + ", zip=" + zip + ")";

        assertEquals(referenceString, address.toString());
    }

    @Test
    void withCity_SameValue_Equal() {
        String newCity = "New City";
        referenceAddress = address.withCity(newCity);

        assertEquals(referenceAddress.getCity(), newCity);
    }

    @Test
    void withCountry_SameValue_Equal() {
        String newCountry = "New Country";
        referenceAddress = address.withCountry(newCountry);

        assertEquals(referenceAddress.getCountry(), newCountry);
    }

    @Test
    void withCounty_SameValue_Equal() {
        String newCounty = "New County";
        referenceAddress = address.withCounty(newCounty);

        assertEquals(referenceAddress.getCounty(), newCounty);
    }

    @Test
    void withState_SameValue_Equal() {
        String newState = "New State";
        referenceAddress = address.withState(newState);

        assertEquals(referenceAddress.getState(), newState);
    }

    @Test
    void withStreet_SameValue_Equal() {
        String newStreet = "New Street";
        referenceAddress = address.withStreet(newStreet);

        assertEquals(referenceAddress.getStreet(), newStreet);
    }

    @Test
    void withZip_SameValue_Equal() {
        String newZip = "New ZIP";
        referenceAddress = address.withZip(newZip);

        assertEquals(referenceAddress.getZip(), newZip);
    }
}