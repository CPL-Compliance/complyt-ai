package com.complyt.v1.handler;

import com.complyt.facade.ComplytGtRatesFacade;
import com.complyt.security.permissions.sales_tax_rates.GtRatesReadPermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.ComplytGtRatesMapper;
import com.complyt.v1.mappers.GtAddressMapper;
import com.complyt.v1.model.ComplytSalesTaxRatesDto;
import com.complyt.v1.model.gt.ComplytGtRatesDto;
import com.complyt.v1.model.gt.GtAddressDto;
import com.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ComplytGtRatesHandler {

    @NonNull
    ComplytGtRatesFacade complytGtRatesFacade;

    @NonNull
    ValidationHandler<GtAddressDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @GtRatesReadPermission
    public Mono<ServerResponse> getGtRatesByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<ComplytGtRatesDto> complytGtRatesDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoValidationHandler.validate(serverRequest))
                .map(GtAddressMapper.INSTANCE::gtAddressDtoToGtAddress)
                .flatMap(complytGtRatesFacade::findByAddress)
                .map(ComplytGtRatesMapper.INSTANCE::complytGstRatesToComplytGstRatesDto)
                .flatMap(complytGstRatesDto -> ContextLogger.observeCtx("<-- Returned Body: " + complytGstRatesDto, log::info)
                        .thenReturn(complytGstRatesDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(complytGtRatesDtoMono, ComplytGtRatesDto.class);
    }
}