package io.complyt.business.webclients.addressvalidations;

import io.complyt.domain.Address;
import io.complyt.domain.here.HereAddressData;
import io.complyt.utils.observability.ContextLogger;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import java.net.URI;
import java.nio.charset.StandardCharsets;

@EqualsAndHashCode
@Slf4j
public class HereAddressValidationClientWrapper extends AddressValidationWebClientWrapperBase {
    public HereAddressValidationClientWrapper(WebClient webClient, String scheme, String host, String path, Pair<String, String> licenseKey) {
        super(webClient, scheme, host, path, licenseKey);
    }

    private Mono<HereAddressData> validateAddress(String zip, String street, String city, String state, String country) {
        StringBuilder hereQureyParamsStringBuilder = queryParamBuilder(zip, street, city, state, country);
        URI uri = buildUri(hereQureyParamsStringBuilder);
        return ContextLogger.observeCtx("<-- Sending request to 'here' with the following query params: " +hereQureyParamsStringBuilder, log::info)
                .then(webClient
                .get()
                .uri(uri)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(HereAddressData.class));
    }

    @Override
    public Mono<HereAddressData> validateAddress(Address address) {
        return validateAddress(address.zip(), address.street(), address.city(), address.state(), address.country());
    }

    private StringBuilder queryParamBuilder(String zip, String street, String city, String state, String country) {
        StringBuilder qureyParamsStringBuilder = new StringBuilder();

        // append all params
        appendStringIfNotNullAndNotEmpty(qureyParamsStringBuilder, zip, "postalCode");
        appendStringIfNotNullAndNotEmpty(qureyParamsStringBuilder, street, "street");
        appendStringIfNotNullAndNotEmpty(qureyParamsStringBuilder, city, "city");
        appendStringIfNotNullAndNotEmpty(qureyParamsStringBuilder, state, "state");
        appendStringIfNotNullAndNotEmpty(qureyParamsStringBuilder, country, "country");

        deleteSemiColonIfNeeded(qureyParamsStringBuilder);

        return qureyParamsStringBuilder;
    }

    private static void deleteSemiColonIfNeeded(StringBuilder qureyParamsStringBuilder) {
        // qq query must not end with ; or it will fail the query
        if (qureyParamsStringBuilder.lastIndexOf(";") == qureyParamsStringBuilder.length() - 1) {
            qureyParamsStringBuilder.deleteCharAt(qureyParamsStringBuilder.lastIndexOf(";"));
        }
    }

    private void appendStringIfNotNullAndNotEmpty(StringBuilder stringBuilder, String strToAppend, String paramName) {
        if(strToAppend != null && !strToAppend.equals("")) {
            String sanitizedValue = strToAppend.replace(";", ""); // Remove semicolons for correctness
            stringBuilder.append(paramName + "=" + sanitizedValue + ";");

        }
    }

    private URI buildUri(StringBuilder qureyParamsStringBuilder) {
        return UriComponentsBuilder.newInstance()
                .encode(StandardCharsets.UTF_8)
                .scheme(scheme)
                .host(host)
                .path(path)
                .queryParam("qq", qureyParamsStringBuilder.toString())
                .queryParam(licenseKey.getValue0(), licenseKey.getValue1())
                .build().toUri();
    }
}
