//package com.complyt.v1.controllers;
//
//import com.complyt.domain.sales_tax.SalesTaxRates;
//import com.complyt.proxies.SalesTaxRatesServiceProxy;
//import com.complyt.security.permissions.transaction.TransactionReadPermission;
//import com.complyt.v1.models.ComplytSalesTaxRatesDto;
//import io.swagger.v3.oas.annotations.Operation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//public class TestController {
//
//    @Autowired
//    SalesTaxRatesServiceProxy salesTaxRatesServiceProxy;
//
////    @Operation(summary = "Testing feign and eureka")
////    @TransactionReadPermission
////    @GetMapping("/test/hi")
////    public Mono<SalesTaxRate> test() {
////        return salesTaxRatesServiceProxy.findByAddress();
////    }
//
//    @Operation(summary = "Testing feign and eureka")
//    @TransactionReadPermission
//    @GetMapping("/test/sales_tax_rates")
//    public Mono<ComplytSalesTaxRatesDto> findByAddress(@RequestParam(name = "state") String state, @RequestParam(name = "country") String country,
//                                              @RequestParam(name = "county") String county, @RequestParam(name = "city") String city,
//                                              @RequestParam(name = "street") String street, @RequestParam(name = "zip") String zip) {
//        return salesTaxRatesServiceProxy.findByAddress(state, country, county, city, street, zip);
//    }
////    @RequestLine("GET /v1/sales_tax_rates")
////    Mono<SalesTaxRate> findByAddress(
////            @QueryParam("state") String state, @QueryParam("country") String country,
////            @QueryParam("county") String county, @QueryParam("city") String city,
////            @QueryParam("street") String street, @QueryParam("zip") String zip
////    );
//
//
////    @Operation(summary = "Testing feign and eureka")
////    @TransactionReadPermission
////    @GetMapping("/test/bye")
////    public Mono<String> bye() {
////        return testServiceProxy.bye();
////    }
//
//}