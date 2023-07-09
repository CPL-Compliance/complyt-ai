package com.complyt.business.sales_tax_web_clients;

import com.complyt.annotations.Generated;
import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.taxjar.TaxJarData;
import com.taxjar.Taxjar;
import com.taxjar.exception.TaxjarException;
import com.taxjar.model.rates.RateResponse;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Generated
public final class TaxJarWebClientWrapper extends SalesTaxWebClientWrapperBase {
    private final Taxjar client;

    public TaxJarWebClientWrapper(Taxjar client) {
        super(null, null, null, null, null);
        this.client = client;
    }

    /*
        We will send city and street as blank in case they are being received as null
        because taxJar api doesn't accept null values as address parameters
     */
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String country) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("country", "US");
            params.put("city", city == null ? "" : city);
            params.put("street", address == null ? "" : address);
            RateResponse res = client.ratesForLocation(zip, params);
            TaxJarData taxJarData = new TaxJarData(res.rate);
            return Mono.just(taxJarData);
        } catch (TaxjarException e) {
            e.printStackTrace();
        }

        return Mono.empty();
    }

    public Mono<SalesTaxData> findByAddress(Address address) {
        return findByAddress(address.zip(), address.street(), address.city(), address.country());
    }

}
