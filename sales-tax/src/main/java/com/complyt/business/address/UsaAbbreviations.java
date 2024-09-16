package com.complyt.business.address;

import java.util.HashMap;
import java.util.Map;

public interface UsaAbbreviations {
    Map<String, String> usaAbbreviationsList = new HashMap<>() {{
        put("US", "USA");
        put("USA", "USA");
        put("U.S.A", "USA");
        put("U.S", "USA");
        put("U.S.", "USA");
        put("UNITED STATES", "USA");
        put("UNITED STATES OF AMERICA", "USA");
        put("_UNITEDSTATES","USA");
        put("United State","USA");
    }};
}