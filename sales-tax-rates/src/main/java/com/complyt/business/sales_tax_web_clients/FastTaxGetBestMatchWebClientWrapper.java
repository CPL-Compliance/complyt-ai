package com.complyt.business.sales_tax_web_clients;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import lombok.EqualsAndHashCode;
import org.javatuples.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@EqualsAndHashCode
public class FastTaxGetBestMatchWebClientWrapper extends SalesTaxWebClientWrapperBase {

    public FastTaxGetBestMatchWebClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> key) {
        super(webClient, scheme, host, path, key);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String city, String county, String state, String zip) {
        URI uri = buildUri(city, county, state, zip);

        return webClient
                .get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(FastTaxGetBestMatchData.class)
                .cast(SalesTaxData.class);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(Address address) {
        return findByAddress(address.city(), address.county(), address.state(), address.zip());
    }

    protected URI buildUri(String city, String county, String state, String zip) {
        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .queryParam(licenseKey.getValue0(), licenseKey.getValue1())
                .queryParam("city", city)
                .queryParam("county", county)
                .queryParam("state", state)
                .queryParam("zip", zip)
                .queryParam("taxtype", "sales")
                .build()
                .toUri();
    }
}