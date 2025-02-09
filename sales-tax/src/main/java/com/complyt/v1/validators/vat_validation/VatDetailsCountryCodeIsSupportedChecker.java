package com.complyt.v1.validators.vat_validation;

import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.business.vat_validation.VatCountryCodesMap;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.StateDto;
import com.complyt.v1.models.customer.exemption.ExemptionDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import com.complyt.v1.validators.body_checkers.StateExistsChecker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class VatDetailsCountryCodeIsSupportedChecker implements DtoBodyChecker<VatDetailsToValidateDto> {

    @Override
    public Flux<String> check(@NonNull VatDetailsToValidateDto vatDetailsToValidateDto) {
        String countryCode = vatDetailsToValidateDto.countryCode();
        return VatCountryCodesMap.codeToCountryMap.containsKey(countryCode.toUpperCase()) ||
                VatCountryCodesMap.countryToCodeMap.containsKey(countryCode.toLowerCase())
                ? Flux.empty()
                : Flux.just(vatDetailsToValidateDto.countryCode() + ": " + DtoErrorMessages.NOT_SUPPORTED_COUNTRY_FORMAT_ERROR);
    }
}