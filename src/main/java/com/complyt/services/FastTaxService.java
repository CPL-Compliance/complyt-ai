package com.complyt.services;

import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@Service
@AllArgsConstructor
public class FastTaxService implements SalesTaxService {

    private RestTemplate fastTaxRestTemplate;

    @Override
    public String findByAddress(String zip, String address, String city, String state) {
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

        //logger.info(uri);
        ResponseEntity<String> responseEntity = fastTaxRestTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return responseEntity.getBody();
    }
}
