package com.complyt.business.transaction;

import com.complyt.v1.exceptions.types.ZipCodeNotValidApiException;
import reactor.core.publisher.Mono;

public interface ZipCodeProcessor {

    static Mono<String> get5DigitZipCode(String zip) {
        String baseZip = zip.split("-")[0];

        if (baseZip.length() == 5) {
            return Mono.just(baseZip);
        } else {
            return Mono.error(ZipCodeNotValidApiException::new);
        }

    }
}
