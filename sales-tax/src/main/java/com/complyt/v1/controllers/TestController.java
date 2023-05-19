//package com.complyt.v1.controllers;
//
//import com.complyt.proxies.TestServiceProxy;
//import com.complyt.security.permissions.transaction.TransactionReadPermission;
//import io.swagger.v3.oas.annotations.Operation;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Mono;
//
//@RestController
//public class TestController {
//
//    @Autowired
//    private TestServiceProxy testServiceProxy;
//
//    @Operation(summary = "Testing feign and eureka")
//    @TransactionReadPermission
//    @GetMapping("/test/hi")
//    public Mono<String> test() {
//        return testServiceProxy.hi().map(s -> s);
//    }
//
//    @Operation(summary = "Testing feign and eureka")
//    @TransactionReadPermission
//    @GetMapping("/test/bye")
//    public Mono<String> bue() {
//        return testServiceProxy.bye();
//    }
//}