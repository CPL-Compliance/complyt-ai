package com.complyt.business.vat_validation;

public interface VatValidationAligner {
     static String removeCountryCodeFromVatNumberIfPresent(String countryCode, String vatNumber) {
         return vatNumber.toUpperCase().startsWith(countryCode.toUpperCase())?
                 vatNumber.substring(countryCode.length()) : vatNumber;
    }

    static String alignCountryCode(String countryCode) {
         return VatCountryCodesMap.countryToCodeMap.getOrDefault(countryCode.toLowerCase(), countryCode)
                 .toUpperCase();
    }

    static String alignCountryName(String countryCode) {
         return VatCountryCodesMap.codeToCountryMap.getOrDefault(countryCode.toUpperCase(), countryCode);
    }
}
