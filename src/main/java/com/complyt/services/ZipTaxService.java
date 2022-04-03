package com.complyt.services;

import com.complyt.domain.SalesTaxData;
import com.complyt.domain.ZipTaxData;
import org.javatuples.Pair;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

public class ZipTaxService extends SalesTaxBase implements SalesTaxService {

    public ZipTaxService(RestTemplate restTemplate, String scheme, String host, String path,
                         Pair<String, String> licenseKey) {
        super(restTemplate, scheme, host, path, licenseKey);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        URI uri = buildUri(zip, address, city, state);

        return getSalesTaxDataMono(uri);
    }

    private Mono<SalesTaxData> getSalesTaxDataMono(URI uri) {
        return WebClient.create().get().uri(uri).accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToMono(ZipTaxData.class).cast(SalesTaxData.class);
    }

    protected URI buildUri(String zip, String address, String city, String state) {
        List<String> params = Arrays.asList(address, city, state, zip);
        String addressParam = String.join("%20", params);

        return UriComponentsBuilder.newInstance().scheme(scheme).host(host).path(path)
                .queryParam(licenseKey.getValue0(), licenseKey.getValue1())
                .queryParam("address", addressParam).build().toUri();
    }
}