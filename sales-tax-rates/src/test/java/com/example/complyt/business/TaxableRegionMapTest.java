package com.example.complyt.business;

import com.complyt.business.TaxableRegionsMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaxableRegionMapTest {

    @Test
    void taxableRegions_Length() {
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.size(), 90);
    }
    
    @Test
    void taxableRegions_RegionsKeysToValueTest() {
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWEST TERRITORIES"), "Northwest Territories");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NUNAVUT"), "Nunavut");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("YUKON"), "Yukon");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITISH COLUMBIA"), "British Columbia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("QUEBEC"), "Quebec");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("MANITOBA"), "Manitoba");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("SASKATCHEWAN"), "Saskatchewan");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("ONTARIO"), "Ontario");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEW BRUNSWICK"), "New Brunswick");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLAND AND LABRADOR"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NOVA SCOTIA"), "Nova Scotia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCE EDWARD ISLAND"), "Prince Edward Island");

        // Misspelled keys
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWES TERRITORIES"), "Northwest Territories");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWEST TERITORIES"), "Northwest Territories");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWEST TERRORITIES"), "Northwest Territories");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWEST TERRITORY"), "Northwest Territories");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWEST TERRITORI"), "Northwest Territories");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWEST TERRITOREIS"), "Northwest Territories");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NORTHWESTTERRITORIES"), "Northwest Territories");


        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NUNUVUT"), "Nunavut");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NUNVAT"), "Nunavut");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NUNAVT"), "Nunavut");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NUNAVUTT"), "Nunavut");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NUNVUT"), "Nunavut");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NUNAVAT"), "Nunavut");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("YUKN"), "Yukon");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("YUON"), "Yukon");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("YUKONN"), "Yukon");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("YUKIN"), "Yukon");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("YUKAN"), "Yukon");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("YAKAN"), "Yukon");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITSH COLUMBIA"), "British Columbia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITISH COLUMIA"), "British Columbia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITISH COLMBIA"), "British Columbia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITISH COLUMIBA"), "British Columbia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITISH COLOMBIA"), "British Columbia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITTISH COLUMBIA"), "British Columbia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("BRITISHCOLUMBIA"), "British Columbia");


        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("QUEBECQ"), "Quebec");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("QUEBEK"), "Quebec");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("QUEBCE"), "Quebec");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("QUEBECK"), "Quebec");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("QUEEBEC"), "Quebec");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("QUBEC"), "Quebec");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("MANITBA"), "Manitoba");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("MANITOAB"), "Manitoba");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("MANITOBAA"), "Manitoba");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("MANITOBO"), "Manitoba");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("MANITOB"), "Manitoba");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("MANATOBA"), "Manitoba");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("SASKATCHWAN"), "Saskatchewan");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("SASKATCHEWAAN"), "Saskatchewan");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("SASKATHEWAN"), "Saskatchewan");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("SASKATCHEN"), "Saskatchewan");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("SASKATCHEWA"), "Saskatchewan");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("SASKATCHEAN"), "Saskatchewan");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("ONTAROI"), "Ontario");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("ONTARIOO"), "Ontario");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("ONTARO"), "Ontario");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("ONTARRIO"), "Ontario");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("ONTRIO"), "Ontario");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("ONTARIOI"), "Ontario");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEW BRUNSWIK"), "New Brunswick");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEW BRUNWICK"), "New Brunswick");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEW BRUNSICK"), "New Brunswick");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEW BRUNSWCK"), "New Brunswick");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEW BRUNSICk"), "New Brunswick");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWBRUNSICK"), "New Brunswick");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLAND AND LABRADAR"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLAND AND LABRADORR"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLAND AND LABRADO"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNLAND AND LABRADOR"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLAND AND LABRDOR"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLANDAND LABRADOR"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLAND ANDLABRADOR"), "Newfoundland and Labrador");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NEWFOUNDLANDANDLABRADOR"), "Newfoundland and Labrador");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NOVA SCOTI"), "Nova Scotia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NOVA SCOIA"), "Nova Scotia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NOVA SCOTAA"), "Nova Scotia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NOVA SCOTIAA"), "Nova Scotia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NOVA SOTIA"), "Nova Scotia");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("NOVASCOTIA"), "Nova Scotia");

        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCE EDWARD ISLND"), "Prince Edward Island");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCE EDWARD ISLANDD"), "Prince Edward Island");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCE EDWARD ISLAN"), "Prince Edward Island");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCE EDWARD ISLNAD"), "Prince Edward Island");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCE EDWARD ISALND"), "Prince Edward Island");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCEEDWARD ISALND"), "Prince Edward Island");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCE EDWARDISALND"), "Prince Edward Island");
        Assertions.assertEquals(TaxableRegionsMap.taxableRegions.get("PRINCEEDWARDISALND"), "Prince Edward Island");
    }
}
