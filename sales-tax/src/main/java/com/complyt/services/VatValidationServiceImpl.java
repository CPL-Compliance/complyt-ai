package com.complyt.services;

import com.complyt.business.timestamps_injection.InternalTimestampsInjector;
import com.complyt.business.vat_validation.VatValidationAligner;
import com.complyt.business.vat_validation.web_clients.VatValidationWebClientWrapper;
import com.complyt.domain.ValidatedVat;
import com.complyt.domain.VatDetailsToValidate;
import com.complyt.repositories.VatValidationRepository;
import com.complyt.utils.observability.ContextLogger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class VatValidationServiceImpl implements VatValidationService {

    @NonNull
    VatValidationWebClientWrapper vatValidationWebClientWrapper;

    @NonNull
    VatValidationRepository vatValidationRepository;

    @NonNull
    InternalTimestampsInjector<ValidatedVat> internalTimestampsInjector;

    @Override
    public Mono<ValidatedVat> findValidatedVat(VatDetailsToValidate vatDetails) {
        VatDetailsToValidate alignedCountryCodeAndVatNumberVatDetails = alignVatDetails(vatDetails);

        return ContextLogger.observeCtx("aligning vat before searching in db:  country code alignment - " + vatDetails.getCountryCode() + " -> " + alignedCountryCodeAndVatNumberVatDetails.getCountryCode() +
                        ", vat number validation - " + vatDetails.getVatNumber() + "->" + alignedCountryCodeAndVatNumberVatDetails.getVatNumber(), log::info)
                .then(vatValidationRepository.find(alignedCountryCodeAndVatNumberVatDetails));
    }

    @Override
    public Mono<ValidatedVat> validate(VatDetailsToValidate vatDetails) {
        VatDetailsToValidate alignedCountryCodeAndVatNumberVatDetails = alignVatDetails(vatDetails);

        return ContextLogger.observeCtx("aligning vat before validating with source: country code alignment - " + vatDetails.getCountryCode() + " -> " + alignedCountryCodeAndVatNumberVatDetails.getCountryCode() +
                        ", vat number validation - " + vatDetails.getVatNumber() + "->" + alignedCountryCodeAndVatNumberVatDetails.getVatNumber(), log::info)
                .then(vatValidationWebClientWrapper.validate(alignedCountryCodeAndVatNumberVatDetails)
                        .flatMap(validatedVatFromService ->
                                // validated vat parameters return as null, the vat country had a spelling mistake - setting as false and returning without saving
                                (isValidatedVatFaulty(validatedVatFromService)) ?
                                        setValidationDetailsToInputAndFalse(vatDetails) :

                                        // if vat validation service returned an answer - saving and returning
                                        Mono.just(validatedVatFromService.setCountryName(VatValidationAligner.alignCountryName(validatedVatFromService.getCountryCode())))
                                                .flatMap(vatAfterValidationFromSourceAndCountryName -> vatAfterValidationFromSourceAndCountryName.getValid() ?
                                                        ContextLogger.observeCtx("vat passed validation -" + vatAfterValidationFromSourceAndCountryName + ", saving to db", log::info)
                                                                .then(setTimestampsAndSaveVat(vatAfterValidationFromSourceAndCountryName))
                                                        : ContextLogger.observeCtx("vat did not passed validation -" + vatAfterValidationFromSourceAndCountryName + ", saving to db", log::info)
                                                        .then(setTimestampsAndSaveVat(vatAfterValidationFromSourceAndCountryName)))));
    }

    private VatDetailsToValidate alignVatDetails(VatDetailsToValidate vatDetailsToValidate) {
        String alignedCountryCode = VatValidationAligner.alignCountryCode(vatDetailsToValidate.getCountryCode());
        String alignedVatNumber = VatValidationAligner.removeCountryCodeFromVatNumberIfPresent(alignedCountryCode, vatDetailsToValidate.getVatNumber());

        return new VatDetailsToValidate(alignedCountryCode, alignedVatNumber);
    }

    /**
     * checking if country code and vat number are null.
     * if they did, the object had failed to instantiate
     */
    private boolean isValidatedVatFaulty(ValidatedVat validatedVatFromService) {
        return validatedVatFromService.getCountryCode() == null ||
                validatedVatFromService.getVatNumber() == null;
    }

    private Mono<ValidatedVat> setTimestampsAndSaveVat(ValidatedVat validatedVat) {
        return Mono.just(internalTimestampsInjector.insertTimestampsToNew(validatedVat))
                .flatMap(validVatWithCountryNameAndTimestamps -> vatValidationRepository.save(validVatWithCountryNameAndTimestamps));
    }

    private Mono<ValidatedVat> setValidationDetailsToInputAndFalse(VatDetailsToValidate inputVatDetails) {
        return  Mono.just(new ValidatedVat(inputVatDetails.getCountryCode(), null, inputVatDetails.getVatNumber(),
                false, null, null, null));
    }
}
