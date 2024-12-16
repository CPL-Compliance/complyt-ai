package io.complyt.business.address;

import java.util.HashMap;
import java.util.Map;

public interface UsaAbbreviations {
    String DEFAULT_COUNTRY = "United States Of America";
    Map<String, String> usaAbbreviationsList = new HashMap<>() {{
        put("US", DEFAULT_COUNTRY);
        put("USA", DEFAULT_COUNTRY);
        put("U.S.A", DEFAULT_COUNTRY);
        put("U.S", DEFAULT_COUNTRY);
        put("U.S.", DEFAULT_COUNTRY);
        put("UNITED STATES", DEFAULT_COUNTRY);
        put("UNITED STATES OF AMERICA", DEFAULT_COUNTRY);
        put("_UNITEDSTATES", DEFAULT_COUNTRY);
        put("United State", DEFAULT_COUNTRY);
    }};
}