package com.complyt.v1.handler;

import com.complyt.facade.SalesTaxRatesFacade;
import com.complyt.observability.ContextLogger;
import com.complyt.security.permissions.SalesTaxRatesReadPermission;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.AddressMapper;
import com.complyt.v1.mappers.SalesTaxRatesMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.SalesTaxRatesDto;
import com.complyt.v1.validators.query_params.QueryParamsExtractor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesTaxRatesHandler {

    @NonNull
    SalesTaxRatesFacade salesTaxRatesFacade;

    @NonNull
    QueryParamsExtractor<AddressDto> addressDtoQueryParamsExtractor;

    @SalesTaxRatesReadPermission
    public Mono<ServerResponse> getSalesTaxRatesByAddress(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxRatesDto> salesTaxRatesDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(addressDtoQueryParamsExtractor.extract(serverRequest))
                .map(AddressMapper.INSTANCE::addressDtoToAddress)
                .flatMap(salesTaxRatesFacade::findByAddress)
                .map(SalesTaxRatesMapper.INSTANCE::salesTaxRatesToSalesTaxRatesDto)
                .flatMap(salesTaxRatesDto -> ContextLogger.observeCtx("<-- Returned Body: " + salesTaxRatesDto, log::info).thenReturn(salesTaxRatesDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(salesTaxRatesDtoMono, SalesTaxRatesDto.class);
    }

}