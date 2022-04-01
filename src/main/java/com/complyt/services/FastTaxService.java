package com.complyt.services;

import com.complyt.domain.FastTaxData;
import com.complyt.domain.SalesTaxData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

public class FastTaxService extends SalesTaxBase implements SalesTaxService {

    public FastTaxService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public SalesTaxData findByAddress(String zip, String address, String city, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("trial.serviceobjects.com")
                .path("ft/web.svc/JSON/GetBestMatch")
                .queryParam("address", address)
                .queryParam("city", city)
                .queryParam("state", state)
                .queryParam("zip", zip)
                .queryParam("taxtype", "sales")
                .queryParam("licensekey", "WS19-KRF3-JGD1")
                .build().toUriString();

        //ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return restTemplate.getForObject(uri, FastTaxData.class);
    }
}
