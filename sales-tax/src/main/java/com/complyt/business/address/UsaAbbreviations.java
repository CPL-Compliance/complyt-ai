package com.complyt.business.address;

import java.util.ArrayList;
import java.util.List;

public interface UsaAbbreviations {
    List<String> usaAbbreviationsList = new ArrayList<>() {{
        add("US");
        add("USA");
        add("U.S.A");
        add("U.S");
        add("U.S.");
        add("UNITED STATES");
        add("UNITED STATES OF AMERICA");
    }};
}