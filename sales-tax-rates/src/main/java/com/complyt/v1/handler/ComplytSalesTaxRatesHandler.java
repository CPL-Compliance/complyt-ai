package com.complyt.v1.handler;

import com.complyt.domain.SalesTaxRatesData;
import com.complyt.domain.enums.RatesStatus;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.facade.SalesTaxRatesFacade;
import com.complyt.security.permissions.sales_tax_rates.SalesTaxRatesReadPermission;
import com.complyt.security.permissions.sales_tax_rates.SalesTaxRatesUpdatePermission;
import com.complyt.utils.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.AddressWithDateMapper;
import com.complyt.v1.mappers.InternalSalesTaxRatesMapper;
import com.complyt.v1.mappers.SalesTaxRatesDataMapper;
import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDataDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
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
public class ComplytSalesTaxRatesHandler {
    @NonNull
    SalesTaxRatesFacade<InternalSalesTaxRates> complytSalesTaxRatesFacade;

    @NonNull
    ValidationHandler<AddressWithDateDto, SpringValidatorAdapter> addressDtoValidationHandler;

    @NonNull
    ValidationHandler<InternalSalesTaxRatesDto, SpringValidatorAdapter> internalRatesDtoValidationHandler;

    @SalesTaxRatesReadPermission
    public Mono<ServerResponse> getSalesTaxRatesByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        boolean detailed = Boolean.parseBoolean(serverRequest.queryParam("detailed").orElse("false"));

        Mono<SalesTaxRatesDataDto> commonSalesTaxRatesDto = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoValidationHandler.validate(serverRequest))
                .map(AddressWithDateMapper.INSTANCE::addressWithDateDtoToAddressDate)
                .flatMap(addressDate -> complytSalesTaxRatesFacade.validateAddress(addressDate, detailed))
                .map(SalesTaxRatesDataMapper.INSTANCE::salesTaxRatesDataTosalesTaxRatesDataDto)
                .flatMap(commonSalesTaxRates -> ContextLogger.observeCtx("<-- Returned Body: " + commonSalesTaxRates, log::info)
                        .thenReturn(commonSalesTaxRates))
                .switchIfEmpty(ContextLogger.observeCtx("Failed to get SalesTaxRates by address", log::error)
                        .then(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(commonSalesTaxRatesDto, SalesTaxRatesData.class);
    }

    @SalesTaxRatesUpdatePermission
    public Mono<ServerResponse> putSalesTaxRatesByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        String status = serverRequest.queryParam("status").orElse(null);

        Mono<InternalSalesTaxRatesDto> commonSalesTaxRatesDto = ContextLogger.observeCtx(logStr, log::info)
                .then(internalRatesDtoValidationHandler.handle(serverRequest))
                .map(InternalSalesTaxRatesMapper.INSTANCE::internalRatesDtoToInternalRates)
                .flatMap(internalRates -> complytSalesTaxRatesFacade.updateRate(internalRates, RatesStatus.valueOf(status)))
                .map(InternalSalesTaxRatesMapper.INSTANCE::internalRatesToInternalRatesDto)
                .flatMap(commonSalesTaxRates -> ContextLogger.observeCtx("<-- Returned Body: " + commonSalesTaxRates, log::info)
                        .thenReturn(commonSalesTaxRates))
                .switchIfEmpty(ContextLogger.observeCtx("Failed to get Internal Rate by address", log::error)
                        .then(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(commonSalesTaxRatesDto, InternalSalesTaxRatesDto.class);
    }
}