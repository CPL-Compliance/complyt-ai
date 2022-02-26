package com.complyt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;

@RestController
public class FastTaxController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/getSalesTax")
    public String getSalesTax(@RequestParam String zip, @RequestParam String address, @RequestParam String city,
                              @RequestParam String state) {
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

        logger.info(uri);
        ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
        return responseEntity.getBody();
    }
}