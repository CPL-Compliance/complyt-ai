package com.complyt.services;

import com.complyt.domain.FastTaxData;
import com.complyt.domain.SalesTaxData;
import org.javatuples.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

public class FastTaxService extends SalesTaxBase implements SalesTaxService {

    public FastTaxService(RestTemplate restTemplate,
                          String scheme,
                          String host,
                          String path,
                          Pair<String, String> licenseKey) {
        super(restTemplate, scheme, host, path, licenseKey);
    }

    @Override
    public FastTaxData findByAddress(String zip, String address, String city, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String uri = buildUri(zip, address, city, state);

        return restTemplate.getForObject(uri, FastTaxData.class);
    }

    protected String buildUri(String zip, String address, String city, String state) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .queryParam(licenseKey.getValue0(), licenseKey.getValue1())
                .queryParam("address", address)
                .queryParam("city", city)
                .queryParam("state", state)
                .queryParam("zip", zip)
                .queryParam("taxtype", "sales")
                .build()
                .toUriString();
    }
}
