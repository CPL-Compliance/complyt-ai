package com.complyt.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
@Primary
@AllArgsConstructor
public class ZipTaxService implements SalesTaxService {

    //Logger logger = LoggerFactory.getLogger(this.getClass());

    private RestTemplate restTemplate;

    @Override
    public String getSalesTax(String zip, String address, String city, String state) {
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

        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return responseEntity.getBody();
    }
}