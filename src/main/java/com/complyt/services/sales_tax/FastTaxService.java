package com.complyt.services.sales_tax;

import com.complyt.domain.FastTaxData;
import com.complyt.domain.SalesTaxData;
import org.javatuples.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

public class FastTaxService extends SalesTaxBase implements SalesTaxService {

    public FastTaxService(WebClient webClient, String scheme, String host, String path, Pair<String, String> key) {
        super(webClient, scheme, host, path, key);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        URI uri = buildUri(zip, address, city, state);
        WebClient webClient = buildWebClient(uri);

        return webClient
                .get()
                .retrieve()
                .bodyToMono(FastTaxData.class)
                .cast(SalesTaxData.class);
    }

    private WebClient buildWebClient(URI uri) {
        return WebClient
                .builder()
                .baseUrl(uri.toString())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    protected URI buildUri(String zip, String address, String city, String state) {
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
                .toUri();
    }
}
