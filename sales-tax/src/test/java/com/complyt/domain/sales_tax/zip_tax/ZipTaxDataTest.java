package com.complyt.domain.sales_tax.zip_tax;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ZipTaxDataTest {

    ZipTaxData zipTaxData;

    @BeforeEach
    void setUp() {
        String version = "version";
        long rCode = 0;
        List<Result> results = new ArrayList<>();
        zipTaxData = new ZipTaxData(version, rCode, results);

        assertNotNull(zipTaxData);
    }

    @Test
    void testToString() {
        String zipTaxDataStr = "ZipTaxData(version=" + zipTaxData.getVersion() + ", rCode=" + zipTaxData.getRCode() + ", results=" + zipTaxData.getResults() + ")";

        assertEquals(zipTaxDataStr, zipTaxData.toString());
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        ZipTaxData anotherZipTaxData = zipTaxData.withRCode(zipTaxData.getRCode());
        assertEquals(zipTaxData, anotherZipTaxData);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        ZipTaxData anotherZipTaxData = zipTaxData.withRCode(zipTaxData.getRCode());
        assertEquals(zipTaxData.hashCode(), anotherZipTaxData.hashCode());
    }

    @Test
    void isUnincorporated_ZipTaxDataIsNotUnincorporated_ReturnsFalse() {
        // Given
        ZipTaxData givenZipTaxData = new ZipTaxData("version", 0L, null);

        // When
        boolean isUnincorporated = givenZipTaxData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }

    @Test
    void noArgsConstructor_ReturnEmptyZipTaxData() {
        // Given + When
        ZipTaxData givenZipTaxData = new ZipTaxData();

        // Then
        assertNull(givenZipTaxData.getVersion());
        assertEquals(0f, givenZipTaxData.getRCode());
        assertNull(givenZipTaxData.getResults());
    }

}
