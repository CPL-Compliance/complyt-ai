package com.complyt.config.web_clients;

import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Value;

public class FastTaxWebClientWrapperProperties {
    public static final String SCHEME = "https";
    public static final String HOST = "ws.serviceobjects.com";
    public static final String PATH = "FT/web.svc/json/GetBestMatch";
    public static final Pair<String, String> KEY = new Pair<>("licensekey", "WS19-HSN4-DXJ2");
}
