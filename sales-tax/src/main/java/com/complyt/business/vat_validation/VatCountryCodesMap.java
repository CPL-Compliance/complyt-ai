package com.complyt.business.vat_validation;

import java.util.HashMap;
import java.util.Map;

public interface VatCountryCodesMap {
    Map<String, String> countryToCodeMap = new HashMap<>() {{
        put("austria", "AT");
        put("belgium", "BE");
        put("bulgaria", "BG");
        put("cyprus", "CY");
        put("czechia", "CZ");
        put("germany", "DE");
        put("denmark", "DK");
        put("estonia", "EE");
        put("greece", "EL");
        put("spain", "ES");
        put("finland", "FI");
        put("france", "FR");
        put("croatia", "HR");
        put("hungary", "HU");
        put("ireland", "IE");
        put("italy", "IT");
        put("lithuania", "LT");
        put("luxembourg", "LU");
        put("latvia", "LV");
        put("malta", "MT");
        put("the netherlands", "NL");
        put("poland", "PL");
        put("portugal", "PT");
        put("romania", "RO");
        put("sweden", "SE");
        put("slovenia", "SI");
        put("slovakia", "SK");
        put("northern ireland", "XI");
    }};

    Map<String, String> codeToCountryMap = new HashMap<>() {{
        put("AT", "Austria");
        put("BE", "Belgium");
        put("BG", "Bulgaria");
        put("CY", "Cyprus");
        put("CZ", "Czechia");
        put("DE", "Germany");
        put("DK", "Denmark");
        put("EE", "Estonia");
        put("EL", "Greece");
        put("ES", "Spain");
        put("FI", "Finland");
        put("FR", "France");
        put("HR", "Croatia");
        put("HU", "Hungary");
        put("IE", "Ireland");
        put("IT", "Italy");
        put("LT", "Lithuania");
        put("LU", "Luxembourg");
        put("LV", "Latvia");
        put("MT", "Malta");
        put("NL", "The Netherlands");
        put("PL", "Poland");
        put("PT", "Portugal");
        put("RO", "Romania");
        put("SE", "Sweden");
        put("SI", "Slovenia");
        put("SK", "Slovakia");
        put("XI", "Northern Ireland");
    }};
}
