package com.complyt.services;

import com.complyt.domain.FastTaxData;
import com.complyt.domain.SalesTaxData;
import org.javatuples.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

public class FastTaxService extends SalesTaxBase implements SalesTaxService {

    public FastTaxService(RestTemplate restTemplate,
                          String scheme,
                          String host,
                          String path,
                          Pair<String, String> licenseKey) {
        super(restTemplate, scheme, host, path, licenseKey);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        URI uri = buildUri(zip, address, city, state);

        return getSalesTaxDataMono(uri);
    }

    private Mono<SalesTaxData> getSalesTaxDataMono(URI uri) {
        return WebClient
                .create()
                .get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(FastTaxData.class)
                .cast(SalesTaxData.class);
    }

    private URI buildUri(String zip, String address, String city, String state) {
        return UriComponentsBuilder.newInstance()
                .path(path)
                .host(host)
                .scheme(scheme)
                .queryParam(licenseKey.getValue0(), licenseKey.getValue1())
                .queryParam("address", address)
                .queryParam("city", city)
                .queryParam("state", state)
                .queryParam("zip", zip)
                .queryParam("taxtype", "sales")
                .build().toUri();
    }
}
