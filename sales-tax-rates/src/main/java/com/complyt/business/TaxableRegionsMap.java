package com.complyt.business;

import java.util.HashMap;
import java.util.Map;

public interface TaxableRegionsMap {
    Map<String, String> taxableRegions = new HashMap<>() {{
        put("NORTHWEST TERRITORIES", "Northwest Territories");
        put("NUNAVUT", "Nunavut");
        put("YUKON", "Yukon");
        put("BRITISH COLUMBIA", "British Columbia");
        put("QUEBEC", "Quebec");
        put("MANITOBA", "Manitoba");
        put("SASKATCHEWAN", "Saskatchewan");
        put("ONTARIO", "Ontario");
        put("NEW BRUNSWICK", "New Brunswick");
        put("NEWFOUNDLAND AND LABRADOR", "Newfoundland and Labrador");
        put("NOVA SCOTIA", "Nova Scotia");
        put("PRINCE EDWARD ISLAND", "Prince Edward Island");

        // Misspelled keys
        put("NORTHWES TERRITORIES", "Northwest Territories");
        put("NORTHWEST TERITORIES", "Northwest Territories");
        put("NORTHWEST TERRORITIES", "Northwest Territories");
        put("NORTHWEST TERRITORY", "Northwest Territories");
        put("NORTHWEST TERRITORI", "Northwest Territories");
        put("NORTHWEST TERRITOREIS", "Northwest Territories");
        put("NORTHWESTTERRITORIES", "Northwest Territories");


        put("NUNUVUT", "Nunavut");
        put("NUNVAT", "Nunavut");
        put("NUNAVT", "Nunavut");
        put("NUNAVUTT", "Nunavut");
        put("NUNVUT", "Nunavut");
        put("NUNAVAT", "Nunavut");

        put("YUKN", "Yukon");
        put("YUON", "Yukon");
        put("YUKONN", "Yukon");
        put("YUKIN", "Yukon");
        put("YUKAN", "Yukon");
        put("YAKAN", "Yukon");

        put("BRITSH COLUMBIA", "British Columbia");
        put("BRITISH COLUMIA", "British Columbia");
        put("BRITISH COLMBIA", "British Columbia");
        put("BRITISH COLUMIBA", "British Columbia");
        put("BRITISH COLOMBIA", "British Columbia");
        put("BRITTISH COLUMBIA", "British Columbia");
        put("BRITISHCOLUMBIA", "British Columbia");


        put("QUEBECQ", "Quebec");
        put("QUEBEK", "Quebec");
        put("QUEBCE", "Quebec");
        put("QUEBECK", "Quebec");
        put("QUEEBEC", "Quebec");
        put("QUBEC", "Quebec");

        put("MANITBA", "Manitoba");
        put("MANITOAB", "Manitoba");
        put("MANITOBAA", "Manitoba");
        put("MANITOBO", "Manitoba");
        put("MANITOB", "Manitoba");
        put("MANATOBA", "Manitoba");

        put("SASKATCHWAN", "Saskatchewan");
        put("SASKATCHEWAAN", "Saskatchewan");
        put("SASKATHEWAN", "Saskatchewan");
        put("SASKATCHEN", "Saskatchewan");
        put("SASKATCHEWA", "Saskatchewan");
        put("SASKATCHEAN", "Saskatchewan");

        put("ONTAROI", "Ontario");
        put("ONTARIOO", "Ontario");
        put("ONTARO", "Ontario");
        put("ONTARRIO", "Ontario");
        put("ONTRIO", "Ontario");
        put("ONTARIOI", "Ontario");

        put("NEW BRUNSWIK", "New Brunswick");
        put("NEW BRUNWICK", "New Brunswick");
        put("NEW BRUNSICK", "New Brunswick");
        put("NEW BRUNSWCK", "New Brunswick");
        put("NEW BRUNSICk", "New Brunswick");
        put("NEWBRUNSICK", "New Brunswick");

        put("NEWFOUNDLAND AND LABRADAR", "Newfoundland and Labrador");
        put("NEWFOUNDLAND AND LABRADORR", "Newfoundland and Labrador");
        put("NEWFOUNDLAND AND LABRADO", "Newfoundland and Labrador");
        put("NEWFOUNLAND AND LABRADOR", "Newfoundland and Labrador");
        put("NEWFOUNDLAND AND LABRDOR", "Newfoundland and Labrador");
        put("NEWFOUNDLANDAND LABRADOR", "Newfoundland and Labrador");
        put("NEWFOUNDLAND ANDLABRADOR", "Newfoundland and Labrador");
        put("NEWFOUNDLANDANDLABRADOR", "Newfoundland and Labrador");

        put("NOVA SCOTI", "Nova Scotia");
        put("NOVA SCOIA", "Nova Scotia");
        put("NOVA SCOTAA", "Nova Scotia");
        put("NOVA SCOTIAA", "Nova Scotia");
        put("NOVA SOTIA", "Nova Scotia");
        put("NOVASCOTIA", "Nova Scotia");

        put("PRINCE EDWARD ISLND", "Prince Edward Island");
        put("PRINCE EDWARD ISLANDD", "Prince Edward Island");
        put("PRINCE EDWARD ISLAN", "Prince Edward Island");
        put("PRINCE EDWARD ISLNAD", "Prince Edward Island");
        put("PRINCE EDWARD ISALND", "Prince Edward Island");
        put("PRINCEEDWARD ISALND", "Prince Edward Island");
        put("PRINCE EDWARDISALND", "Prince Edward Island");
        put("PRINCEEDWARDISALND", "Prince Edward Island");
    }};
}
