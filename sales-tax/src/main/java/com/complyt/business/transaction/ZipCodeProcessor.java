package com.complyt.business.transaction;

import com.complyt.v1.exceptions.types.ZipCodeNotValidApiException;
import reactor.core.publisher.Mono;

public interface ZipCodeProcessor {

    static Mono<String> getBaseZipCode(String zip) {
        String baseZip = zip.trim().split("-")[0];

        if (baseZip.length() == 5) {
            return Mono.just(baseZip);
        } else if (baseZip.length() == 4) {
            return Mono.just("0" + baseZip);
        } else {
            return Mono.error(ZipCodeNotValidApiException::new);
        }

    }

    static Mono<String> getPaddedZipCode(String zip) {
        String baseZip = zip.trim().split("-")[0];

        if (baseZip.length() == 5) {
            return Mono.just(zip.trim());
        } else if (baseZip.length() == 4) {
            return Mono.just("0" + zip.trim());
        } else {
            return Mono.error(ZipCodeNotValidApiException::new);
        }

    }
}
