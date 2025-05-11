package com.complyt.proxies;

import com.complyt.business.tax.sales_tax.models.ComplytInternalSalesTaxRatesDto;
import com.complyt.v1.models.tax.global_tax.ComplytGtRatesDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "SALES-TAX-RATES")
public interface SalesTaxRatesServiceProxy {

    @GetMapping("/v1/sales_tax_rates")
    Mono<ComplytInternalSalesTaxRatesDto> findByAddress(
            @RequestParam(name = "state") String state, @RequestParam(name = "country") String country,
            @RequestParam(name = "county") String county, @RequestParam(name = "city") String city,
            @RequestParam(name = "street") String street, @RequestParam(name = "zip") String zip,
            @RequestParam(name = "isPartial") boolean isPartial,
            @RequestParam(name="effectiveDate") String transactionDate,
            @RequestParam(name = "detailed") boolean detailed
            );

    @GetMapping("/v1/gt_rates")
    Mono<ComplytGtRatesDto> findGtByAddress(
            @RequestParam(name = "country") String country,
            @RequestParam(name = "region") String region
    );

}