package com.complyt.business.sales_tax.sales_tax_web_clients;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
import lombok.EqualsAndHashCode;
import org.javatuples.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode
public class ZipTaxWebClientWrapper extends SalesTaxWebClientWrapperBase implements SalesTaxWebClientWrapper {

    public ZipTaxWebClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> key) {
        super(webClient, scheme, host, path, key);
    }

    @Override
    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        URI uri = buildUri(zip, address, city, state);

        return webClient
                .get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(ZipTaxData.class)
                .cast(SalesTaxData.class).log();
    }

    @Override
    public Mono<SalesTaxData> findByAddress(Address address) {
        return findByAddress(address.getZip(), address.getStreet(), address.getCity(), address.getState());
    }

    protected URI buildUri(String zip, String address, String city, String state) {
        List<String> params = Arrays.asList(address, city, state, zip);
        String addressParam = String.join(",", params);

        return UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .path(path)
                .queryParam(licenseKey.getValue0(), licenseKey.getValue1())
                .queryParam("address", addressParam)
                .build()
                .toUri();
    }
}