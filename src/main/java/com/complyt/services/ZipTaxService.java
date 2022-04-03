package com.complyt.services;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.ZipTaxData;
import org.javatuples.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

public class ZipTaxService extends SalesTaxBase implements SalesTaxService {

    public ZipTaxService(RestTemplate restTemplate, String scheme, String host, String path, Pair<String, String> licenseKey) {
        super(restTemplate, scheme, host, path, licenseKey);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        String uri = buildUri(zip, address, city, state);

        return WebClient.create().get().uri(uri).accept(MediaType.APPLICATION_JSON).retrieve().bodyToMono(ZipTaxData.class).cast(SalesTaxData.class);

        //        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        String uri = buildUri(zip, address, city, state);
//
//        return restTemplate.getForObject(uri, ZipTaxData.class);
    }

    protected String buildUri(String zip, String address, String city, String state) {
        List<String> params = Arrays.asList(address, city, state, zip);
        String addressParam = String.join("%20", params);

        return UriComponentsBuilder.newInstance().scheme(scheme).host(host).path(path).queryParam(licenseKey.getValue0(), licenseKey.getValue1()).queryParam("address", addressParam).build().toUriString();
    }
}