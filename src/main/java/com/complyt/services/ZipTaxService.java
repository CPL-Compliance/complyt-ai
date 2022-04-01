package com.complyt.services;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.ZipTaxData;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

public class ZipTaxService extends SalesTaxBase implements SalesTaxService {

    public ZipTaxService(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public SalesTaxData findByAddress(String zip, String address, String city, String state) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<String> params = Arrays.asList(address, city, state, zip);
        String combinedString = String.join("%20", params);
        String uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api.zip-tax.com")
                .path("request/v40")
                .queryParam("key", "jkRvcDF9MVB5pxtm")
                .queryParam("address", combinedString)
                .build().toUriString();
        System.out.println(uri);

        return restTemplate.getForObject(uri, ZipTaxData.class);
    }
}