package com.complyt.business.sales_tax;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.javatuples.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.net.URI;

public class FastTaxWebClientWrapper extends SalesTaxWebClientWrapperBase implements SalesTaxWebClientWrapper {

    public FastTaxWebClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> key) {
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


    @SneakyThrows
    @Override
    public SalesTaxData findByAddressSync(@NonNull Address address) {
        String json = "{\"MatchLevel\": \"Address\",\"TaxInfoItems\": [{\"City\": \"Englewood\",\"CityDistrictRate\": \"0\",\"CityRate\": \"0.035\",\"County\": \"Arapahoe\",\"CountyDistrictRate\": \"0.011\",\"CountyRate\": \"0.0025\",\"InformationComponents\": [{\"Name\": \"CountyFIPS\",\"Value\": \"005\"}],\"NotesCodes\": \"1\",\"NotesDesc\": \"IsUnincorporated\",\"SpecialDistrictRate\": \"0\",\"StateAbbreviation\": \"CO\",\"StateName\": \"Colorado\",\"StateRate\": \"0.029\",\"TaxRate\": \"0.0775\",\"TotalTaxExempt\": \"LABOR/SERVICES\",\"Zip\": \"80112\"}]}";
        ObjectMapper objectMapper = new ObjectMapper();
        FastTaxData fastTaxData = objectMapper.readValue(json, FastTaxData.class);
        System.out.println(fastTaxData);

        return fastTaxData;    }

    @Override
    public Mono<SalesTaxData> findByAddress(Address address) {
        return Mono.fromCallable(() -> {
            String json = "{\"MatchLevel\": \"Address\",\"TaxInfoItems\": [{\"City\": \"Englewood\",\"CityDistrictRate\": \"0\",\"CityRate\": \"0.035\",\"County\": \"Arapahoe\",\"CountyDistrictRate\": \"0.011\",\"CountyRate\": \"0.0025\",\"InformationComponents\": [{\"Name\": \"CountyFIPS\",\"Value\": \"005\"}],\"NotesCodes\": \"1\",\"NotesDesc\": \"IsUnincorporated\",\"SpecialDistrictRate\": \"0\",\"StateAbbreviation\": \"CO\",\"StateName\": \"Colorado\",\"StateRate\": \"0.029\",\"TaxRate\": \"0.0775\",\"TotalTaxExempt\": \"LABOR/SERVICES\",\"Zip\": \"80112\"}]}";
            ObjectMapper objectMapper = new ObjectMapper();
            FastTaxData fastTaxData = objectMapper.readValue(json, FastTaxData.class);
            System.out.println(fastTaxData);

            return fastTaxData;
        });
//        return Mono.just(
//        String json = "{\"MatchLevel\": \"Address\",\"TaxInfoItems\": [{\"City\": \"Englewood\",\"CityDistrictRate\": \"0\",\"CityRate\": \"0.035\",\"County\": \"Arapahoe\",\"CountyDistrictRate\": \"0.011\",\"CountyRate\": \"0.0025\",\"InformationComponents\": [{\"Name\": \"CountyFIPS\",\"Value\": \"005\"}],\"NotesCodes\": \"1\",\"NotesDesc\": \"IsUnincorporated\",\"SpecialDistrictRate\": \"0\",\"StateAbbreviation\": \"CO\",\"StateName\": \"Colorado\",\"StateRate\": \"0.029\",\"TaxRate\": \"0.0775\",\"TotalTaxExempt\": \"LABOR/SERVICES\",\"Zip\": \"80112\"}]}";
//        ObjectMapper objectMapper = new ObjectMapper();
//        System.out.println("In FastTaxWebClientWrapper::findByAddress");
//        try {
//            FastTaxData fastTaxData = objectMapper.readValue(json, FastTaxData.class);
//            System.out.println(fastTaxData);
//            Mono<SalesTaxData> salesTaxDataMono = Mono.just(fastTaxData).cast(SalesTaxData.class);
//
//            return salesTaxDataMono;
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        });
//
//        return null;
        //return findByAddress(address.getZip(), address.getStreet(), address.getCity(), address.getState());
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
