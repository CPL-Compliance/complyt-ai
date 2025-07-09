package io.complyt.domain.sales_tax;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FilingMetaDataTest {

    @Test
    void testFilingMetaDataCreationAndGetters() {
        FilingMetaData metaData = new FilingMetaData(
                "TestCity",
                "TestCounty",
                BigDecimal.valueOf(1.1),
                BigDecimal.valueOf(2.2),
                BigDecimal.valueOf(3.3),
                BigDecimal.valueOf(4.4),
                "CRPT",
                "CTY",
                "MTA_NAME",
                "MTA_001",
                "SPD_NAME",
                "SPD_001",
                "Other1",
                "O1",
                "Other2",
                "O2",
                "Other3",
                "O3",
                "Other4",
                "O4",
                "12345"
        );

        assertEquals("TestCity", metaData.city());
        assertEquals("TestCounty", metaData.county());
        assertEquals(BigDecimal.valueOf(1.1), metaData.other1Rate());
        assertEquals(BigDecimal.valueOf(2.2), metaData.other2Rate());
        assertEquals(BigDecimal.valueOf(3.3), metaData.other3Rate());
        assertEquals(BigDecimal.valueOf(4.4), metaData.other4Rate());
        assertEquals("CRPT", metaData.countyRptCode());
        assertEquals("CTY", metaData.cityRptCode());
        assertEquals("MTA_NAME", metaData.mtaName());
        assertEquals("MTA_001", metaData.mtaNumber());
        assertEquals("SPD_NAME", metaData.spdName());
        assertEquals("SPD_001", metaData.spdNumber());
        assertEquals("Other1", metaData.other1Name());
        assertEquals("O1", metaData.other1Number());
        assertEquals("Other2", metaData.other2Name());
        assertEquals("O2", metaData.other2Number());
        assertEquals("Other3", metaData.other3Name());
        assertEquals("O3", metaData.other3Number());
        assertEquals("Other4", metaData.other4Name());
        assertEquals("O4", metaData.other4Number());
        assertEquals("12345", metaData.fipsCounty());
    }

    @Test
    void testFilingMetaDataNullSafety() {
        FilingMetaData metaData = new FilingMetaData(
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null, null, null, null,
                null, null, null
        );

        assertNull(metaData.city());
        assertNull(metaData.county());
        assertNull(metaData.other1Rate());
        assertNull(metaData.other2Rate());
        assertNull(metaData.other3Rate());
        assertNull(metaData.other4Rate());
        assertNull(metaData.countyRptCode());
        assertNull(metaData.cityRptCode());
        assertNull(metaData.mtaName());
        assertNull(metaData.mtaNumber());
        assertNull(metaData.spdName());
        assertNull(metaData.spdNumber());
        assertNull(metaData.other1Name());
        assertNull(metaData.other1Number());
        assertNull(metaData.other2Name());
        assertNull(metaData.other2Number());
        assertNull(metaData.other3Name());
        assertNull(metaData.other3Number());
        assertNull(metaData.other4Name());
        assertNull(metaData.other4Number());
        assertNull(metaData.fipsCounty());
    }
}
