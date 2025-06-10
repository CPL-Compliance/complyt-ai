package com.complyt.business;

import java.util.HashMap;
import java.util.Map;

// List of different countries between address validation and sales tax rates
// The key on the left is the country gotten from address validation and the value on the right is the matching value in sales tax rates database
public interface TaxableCountryMap {
    Map<String, String> countryMap = new HashMap<>() {{
        put("AFGHANISTAN", "Afganistan");
        put("BONAIRE", "Bonaire, sint eustatius and saba");
        put("CAYMAN ISLANDS", "Canary Islands");
        put("COOK ISLANDS", "Cocos islands");
        put("CURACAO", "Curaçao");
        put("REPUBLIC OF THE CONGO", "Congo, Republic");
        put("DEMOCRATIC REPUBLIC OF THE CONGO", "Congo, democratic republic of the");
        put("COTE D'IVOIRE", "Côte d'ivoire");
        put("HONG KONG SAR", "Hong kong");
        put("NORTH KOREA", "Korea");
        put("LAOS", "Lao people's democratic republic");
        put("MACAU SAR", "Macao");
        put("REUNION", "Réunion");
        put("RUSSIA", "Russian federation");
        put("SAINT BARTHELEMY", "Saint barthélemy");
        put("ST HELENA, ASCENSION, AND T. CUNHA", "Saint helena, ascension and tristan da cunha");
        put("SOUTH GEORGIA AND S. SANDWICH IS.", "South georgia and the south sandwich islands");
        put("SINT MAARTEN", "St Martin");
        put("SYRIA", "Syrian arab republic");
        put("TANZANIA", "Tanzania, united republic of");
        put("TURKIYE", "Turkey");
    }};
}